package com.virtualpuffer.netdisk.service.impl.file;


import com.virtualpuffer.netdisk.entity.file.*;
import com.virtualpuffer.netdisk.mapper.netdiskFile.DirectoryMap;
import com.virtualpuffer.netdisk.mapper.netdiskFile.FileHashMap;
import com.virtualpuffer.netdisk.mapper.netdiskFile.FileMap;
import com.virtualpuffer.netdisk.mapper.user.UserMap;
import com.virtualpuffer.netdisk.utils.MybatisConnect;
import com.virtualpuffer.netdisk.data.FileCollection;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.utils.Message;
import com.virtualpuffer.netdisk.utils.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * 只处理文件操作，不涉及用户信息
 * 物理内存优先，如果网盘内已有完全一样的版本
 * 则不再上传文件，只在映射表中创建/添加映射
 *
 * 只有两种情况：  1.在仓库的物理内存里
 *              2.在映射表中，映射表使用独立的仓库
 *
 * @method 获取文件路径时，查询映射表并查询物理路径，合并后回传
 * 下载时，如果没有物理路径则在映射表中查询，没有则抛出异常
 *
 *
 * @para user           用户对象，生成类时注入
 * @para file           文件对象，可能是不存在的，让Controller层处理
 * @para path           文件的绝对路径
 * @para destination    文件的相对路径
* */
//@Async
@Service
public class FileBaseService extends FileUtilService {
    protected User user;
    protected File file;
    private Map<Integer,AbsoluteNetdiskDirectory> directoryMap;
    protected AbsoluteNetdiskEntity netdiskEntity;
    protected AbsoluteNetdiskFile netdiskFile;
    protected AbsoluteNetdiskDirectory netdiskDirectory;
    protected String tokenTag;
    protected int file_length;
    private static final Class SQL_LOCK = FileBaseService.class;
    protected boolean isMapper = false;
    protected static final int BUFFER_SIZE = 4 * 1024;
    public static final String downloadAPI = Message.getMess("downloadAPI");//下载链接前缀
    public static final String defaultWare = Message.getMess("defaultWare");
    public static final String duplicateFileWare = Message.getMess("duplicateFileWare");
    public static final String tempWare = getMess("compressTemp");

    public static final String DOWNLOAD_TAG = "1";
    public static final String A_TAG = "";
    public FileBaseService(){}

    /**
    * 构造服务对象
     * @param netdiskEntity 目标文件对象
     * @param user 用户对象
     * 没有处理404情况，controller级别再获取
    * */
    public FileBaseService(AbsoluteNetdiskEntity netdiskEntity, User user) throws FileNotFoundException {
    /*    if(netdiskEntity == null){
            throw new RuntimeException("服务构建失败：null");
        }*/
        this.user = user;
        this.netdiskEntity = netdiskEntity;
        if(netdiskEntity instanceof AbsoluteNetdiskFile){
            AbsoluteNetdiskFile netdiskFile = (AbsoluteNetdiskFile) netdiskEntity;
            this.netdiskFile = netdiskFile;
            this.file = netdiskFile.getFile();
        }else {
            AbsoluteNetdiskDirectory netdiskDirectory = (AbsoluteNetdiskDirectory)netdiskEntity;
            this.netdiskDirectory = netdiskDirectory;
        }
    }

    @Deprecated
    public static FileBaseService getInstanceByPath(String path, int userID) throws FileNotFoundException{
        SqlSession session = MybatisConnect.getSession();
        User user = session.getMapper(UserMap.class).getUserByID(userID);
        AbsoluteNetdiskFile netdiskFile = new AbsoluteNetdiskFile(path);
        return new FileBaseService(netdiskFile,user);
    }
    public static FileBaseService getInstance(String destination, int userID,int priviledge) throws FileNotFoundException{
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            if(destination == null){
                throw new FileNotFoundException("缺少参数:destination");
            }else if(!destination.startsWith("/")){//防止路径没/
                destination = new StringBuffer().append("/").append(destination).toString();
            }else if(destination.contains("..")){
                throw new RuntimeException("路径非法");
            }
            destination = StringUtils.filePathDeal(destination);
            User user = session.getMapper(UserMap.class).getUserByID(userID);
            return getInstance(destination,user,priviledge);
        } finally {
            close(session);
        }
    }
    /**
    * 顶级路径（仓库位置）的id是-1
     * 名字是.
     * 请求方式是destination = ./
     * userid是用户id
    * */
    public static FileBaseService getInstance(String destination, User user,int priviledge) throws FileNotFoundException{
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            if(destination == null){
                throw new FileNotFoundException("缺少参数:destination");
            }else if(!destination.startsWith("/")){//防止路径没/
                destination = new StringBuffer().append("/").append(destination).toString();
            }else if(destination.contains("..")){
                throw new RuntimeException("路径非法");
            }
            //destination = StringUtils.filePathDeal(destination);
            try{
                AbsoluteNetdiskDirectory netdiskDirectory = AbsoluteNetdiskDirectory.getInstance(destination,user.getUSER_ID(),priviledge);
                return new FileBaseService(netdiskDirectory,user);
            }catch(FileNotFoundException e){
                AbsoluteNetdiskFile netdiskFile = AbsoluteNetdiskFile.getInstance(destination,user.getUSER_ID(),priviledge);
                netdiskFile.setFile(new File(netdiskFile.getFile_Path()));
                return new FileBaseService(netdiskFile,user);
            }
        } finally {
            close(session);
        }
    }

    /**
    * 物理路径计算
     * @param destination 相对路径位置（网盘）
    * */
    public String getAbsolutePath(String destination){
        return defaultWare + this.user.getURL() + destination;
    }
    /**
     * 下载链接获取
     * 全部只给map_id(移动了就没了)
     *
     * 映射型文件是系统没办法判断类型的（因为不存在）
    * */
    public static String getDownloadURL(DownloadCollection collection,User user,int priviledge) throws FileNotFoundException {
        Map<String,Object> map = new HashMap();
        ArrayList<Integer> file_id = new ArrayList<>();
        ArrayList<Integer> dir_id = new ArrayList<>();
        String path = collection.getDestination();
        String file_name = "";
        AbsoluteNetdiskFile file = null;
        AbsoluteNetdiskDirectory directory = null;
        if(collection.getFiles().length == 0){
            throw new RuntimeException("未找到下载参数");
        }
        if(path == null || path == ""){
        }else {
            path = StringUtils.filePathDeal(path + "/");
        }
        for(String name : collection.getFiles()){
            String destination =  path + name;
            FileBaseService baseService = getInstance(destination,user,priviledge);
            if (baseService.netdiskDirectory == null) {
                AbsoluteNetdiskFile netdiskFile = baseService.getNetdiskFile();
                file = netdiskFile;
                file_id.add(netdiskFile.getMap_id());
            }else {
                AbsoluteNetdiskDirectory netdiskDirectory = baseService.getNetdiskDirectory();
                directory = netdiskDirectory;
                dir_id.add(netdiskDirectory.getDirectory_ID());
            }
        }
        if(file_id.size() == 1 && dir_id.size() == 0){
            file_name = file.getFile_Name();
        }else if(!dir_id.isEmpty()){
            file_name = directory.getDirectory_Name() + "等"+ (dir_id.size() + file_id.size()) +"个文件.zip";
        }else {
            file_name = file.getFile_Name() + "等"+ (dir_id.size() + file_id.size()) +"个文件.zip";
        }
        map.put("straight",file_id.size() == 1 && dir_id.size() == 0 ? true : false);
        map.put("file_name",file_name);
        map.put("file_id",file_id);
        map.put("dir_id",dir_id);
        map.put("tokenTag",DOWNLOAD_TAG);
        map.put("userID",user.getUSER_ID());
        if (collection.getKey() == null) {
            return  downloadAPI + createToken(collection.getSecond(),map,user.getUsername(),collection.getKey());
        } else {
            return  downloadAPI + "key/" + createToken(collection.getSecond(),map,user.getUsername(),collection.getKey());
        }
    }

    /**
     * 获取路径下文件
     * */
    public Map<String,LinkedList<String>> getDirectory(int priviledge) throws NoSuchFileException {
        if(this.netdiskEntity instanceof AbsoluteNetdiskDirectory){
            return ((AbsoluteNetdiskDirectory) this.netdiskEntity).getDir(priviledge);
        }else{
            throw new RuntimeException("目标不是文件夹");
        }
    }
    /**
     * 文件上传
     * 发现相同文件后将文件移到独立仓库并建立文件映射
     * */
    //SHA校验
    public boolean checkDuplicate(InputStream inputStream) throws Exception{
        return checkDuplicate(getSH256(inputStream));
    }
    //重复返回true
    public boolean checkDuplicate(String hash){
        SqlSession session = null;
        LinkedList list;
        try {
            session = MybatisConnect.getSession();
            list = session.getMapper(FileHashMap.class).checkDuplicate(hash);
        } finally {
            close(session);
        }
        return !list.isEmpty();
    }

    /*public boolean duplicateUpload(String hash,String)*/
    public long downloadFile(OutputStream outputStream) throws Exception {
        InputStream inputStream = null;
        ZipOutputStream zipOutputStream = null;
        try {
        if(this.netdiskEntity instanceof AbsoluteNetdiskDirectory){
            zipOutputStream = new ZipOutputStream(outputStream);
            compress(this.file,zipOutputStream,this.netdiskDirectory.getDirectory_Name());
            zipOutputStream.finish();
            return count();
        }else {
                inputStream = new FileInputStream(this.file);
                int length = inputStream.available();
                copy(inputStream,outputStream);
                return length;
        }
        } finally {
            close(inputStream);
        }
    }
    /**   __________________________记住把substring机制改回去
     * 操作流程：
     *  获取输入流
     *  计算sha256
     *  在file hashmap表中看看有没有已经存在的文件
     *  如果有，把该文件移到重复仓库中
     *  给源文件拥有者和上传者分别建立映射
     *  然后修改file hashmap路径使其指向当前文件位置
     *
     *  如果存在重复的名字，会首先校验sha256
     *  如果结果相同，则不在上传
     *  结果不同则上传并在文件后面加上（1）
     *
     *  hashmap 维护文件物理路径和hash值的对应关系
     *  map     维护hash值和文件拥有者以及相对路径的对应关系
     *
     * @warn 多线程安全警告
     *
    * @param input 上传文件的输入流
    * */
    public void uploadFile(InputStream input)throws RuntimeException,Exception{
        OutputStream outputStream;
        InputStream[] inputStreams = copyStream(input);
        InputStream inputStream = inputStreams[1];
        String hash = getSH256(inputStreams[0]);
        SqlSession session = MybatisConnect.getSession();

        dumplicateParse(hash);
        try {
            if (checkDuplicate(hash)) {
                session.getMapper(FileMap.class).buildFileMap(this.file.getName(),
                        hash,this.user.getUSER_ID(),this.netdiskDirectory.getDirectory_ID());
                session.commit();
            }else {
                String hashFile_path = duplicateFileWare + hash;//map里面的位置
                outputStream = new FileOutputStream(hashFile_path);
                int length = inputStream.available();
                try {
                    copy(inputStream,outputStream);
                } catch (IOException e) {
                    //日志工厂
                    e.printStackTrace();
                } finally {
                    close(outputStream);
                }
                //复制成功？
                if(new File(hashFile_path).exists()){
                    synchronized (SQL_LOCK){
                        if(!checkDuplicate(hash)){
                            session.getMapper(FileMap.class)
                                    .buildFileMap(this.file.getName(),hash,this.user.getUSER_ID(),this.netdiskDirectory.getDirectory_ID());
                            session.getMapper(FileHashMap.class).addHashMap(hash,hashFile_path,user.getUSER_ID(),length);
                            session.commit();
                        }
                    }
                }else {
                    throw new RuntimeException("文件写入失败");
                }
            }
        } finally {
            close(inputStream);
            close(session);
        }
    }
    //上面的工具类，有重复文件时在后缀前加上（1），如果已经存在就递增
    public void dumplicateParse(String hash) throws Exception {
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            AbsoluteNetdiskDirectory directory = session.getMapper(DirectoryMap.class).onExists(user.getUSER_ID(),this.netdiskDirectory.getDirectory_ID(),this.file.getName());
            AbsoluteNetdiskFile netdiskFile = session.getMapper(FileMap.class).getFileMap(user.getUSER_ID(),this.netdiskDirectory.getDirectory_ID(),this.file.getName());
            //重名文件处理
            if (netdiskFile!=null) {
                File testFile = new File(netdiskFile.getFile_Path());
                if(testFile.exists()){
                    if(hash.equals(getSH256(testFile))){
                        throw new RuntimeException("file has been exit");
                    }else {
                        String destination = StringUtils.duplicateRename(this.file.getName());
                        this.file = new File(destination);
                        dumplicateParse(hash);//再来一次
                    }
                }
            }else if(directory != null){
                String destination = StringUtils.duplicateRename(this.file.getName());
                this.file = new File(destination);
                dumplicateParse(hash);//再来一次
            }
        } finally {
            close(session);
        }
    }

    /**
     * 文件删除
     * 检查映射表，存在则删除
     * 映射表不存在，检查
     * */
    public void deleteFileMap(){
       this.netdiskEntity.delete(null);
    }

    public long count() throws RuntimeException {
        ZipOutputStream zos = null;
        String fileName = this.netdiskFile.getFile_Name();
        String path = tempWare + fileName.hashCode();
        File temp = null;
        try {
            temp = new File(path);
            try {
                temp.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("预压缩失败");
            }
            try {
                OutputStream out = new FileOutputStream(path);
                zos = new ZipOutputStream(out);
                compress(this.file, zos, fileName);
                zos.finish();
                zos.flush();
            } catch (Exception e) {
                log.errorLog(e.getMessage());
                throw new RuntimeException("zip error from ZipUtils", e);
            } finally {
                close(zos);
            }
            long ret = temp.length();

            return ret;
        }finally {
            temp.delete();
        }
    }

    public FileCollection searchFile(String name,String type) throws FileNotFoundException{
        SqlSession session = null;
        if(name == null || name.equals("")){
            throw  new RuntimeException("缺少参数：name");
        }
        try {
           session = MybatisConnect.getSession();
           FileCollection collection = new FileCollection();
           LinkedList<AbsoluteNetdiskFile> fileList = session.getMapper(FileMap.class).searchFile(name, user.getUSER_ID(),3);//这有问题，权限给了没搜，暂时不管了
           LinkedList<AbsoluteNetdiskDirectory> dirList = session.getMapper(DirectoryMap.class).searchDir(name, user.getUSER_ID());

           LinkedList<String> file = new LinkedList<>();
           LinkedList<String> dir = new LinkedList<>();

           this.directoryMap = session.getMapper(DirectoryMap.class).getDirMap(this.user.getUSER_ID());
            if(!fileList.isEmpty()){
                for(AbsoluteNetdiskFile fileMap : fileList) {
                    try {
                        AbsoluteNetdiskDirectory directoryParent = directoryMap.get(fileMap.getDirectory_Parent_ID());
                        String file_path = buildAbstractPath(fileMap);
                        file.add(file_path);
                    } catch (RuntimeException e) {
                        //不用管，抛出就是权限不足
                    }
                }
            }
            if(!dirList.isEmpty()){
                for(AbsoluteNetdiskDirectory directory : dirList){
                    try {
                        dir.add(buildAbstractPath(directory));
                    } catch (RuntimeException e) {
                        //不用管，抛出就是权限不足
                    }
                }
            }
            if(file.isEmpty()&&dir.isEmpty()){
                collection.setMsg("没有找到匹配的文件");
                collection.setCode(300);
            }else {
                collection.setMsg("已找到匹配的文件");
                collection.setCode(200);
            }
            collection.setFiles(file);
            collection.setDir(dir);
            return collection;
        } finally {
            close(session);
        }
    }

    public String buildAbstractPath(AbsoluteNetdiskFile netdiskFile)throws RuntimeException{
        AbsoluteNetdiskDirectory directoryParent = directoryMap.get(netdiskFile.getDirectory_Parent_ID());
        String dir_path = buildAbstractPath(directoryParent);
        String path = (netdiskFile == null) ? dir_path : (dir_path + "/" + netdiskFile.getFile_Name());
        return StringUtils.filePathDeal(path);
    }

    public String buildAbstractPath(AbsoluteNetdiskDirectory netdiskDirectory)throws RuntimeException{
        LinkedList<String> pathList = new LinkedList<>();
        StringBuffer buffer = new StringBuffer();
        while (netdiskDirectory.getDirectory_Parent_ID() != -1){
            if(netdiskDirectory.getPriviledge() > AbsoluteNetdiskDirectory.default_priviledge){
                throw new RuntimeException("文件访问权限不足");
            }
            pathList.addFirst(netdiskDirectory.getDirectory_Name());
            netdiskDirectory = directoryMap.get(netdiskDirectory.getDirectory_Parent_ID());
        }
        for(String path : pathList){
            buffer.append("/").append(path);
        }
        return buffer.toString();
    }

    public void mkdir(String name,int priviledge) throws RuntimeException{
        if(name == null || "".equals(name)){
            throw new RuntimeException("文件夹名字不能为空");
        }
        if(this.netdiskEntity instanceof AbsoluteNetdiskDirectory){
            AbsoluteNetdiskDirectory directory = (AbsoluteNetdiskDirectory) this.netdiskEntity;
            directory.mkdir(name,priviledge);
        }else {
            throw new RuntimeException("目标不是文件夹");
        }
    }

    //转存
    public void transfer()throws Exception{
        AbsoluteNetdiskFile netdiskFile = (AbsoluteNetdiskFile) netdiskEntity;

        SqlSession session = null;
        String hash = netdiskFile.getFile_Hash();
        String dest = netdiskFile.getFile_Destination();
        String place = dest.substring(0,dest.lastIndexOf("/")+1);
        AbsoluteNetdiskDirectory directory = (AbsoluteNetdiskDirectory)netdiskEntity;

        dumplicateParse(hash);
        try {
            session = MybatisConnect.getSession();
            session.getMapper(FileMap.class).buildFileMap(
                    netdiskFile.getFile_Name(),hash,this.user.getUSER_ID(),directory.getDirectory_ID());
            session.commit();
        } finally {
            close(session);
        }
    }

    /**
     * 源文件获取zip键值对文件集合
     * 用Getname获取路径
     * 没有则创建路径
     * zip文件解压
     */
    @Deprecated
    public void deCompress() throws FileNotFoundException, IOException ,Exception {
        File file = netdiskFile.getFile();
       // deCompress(file,netdiskFile.getFile_Destination_Place());
    }
    protected void deCompress(File srcFile,String destDirPath) throws FileNotFoundException,IOException,Exception{
        SqlSession session = null;
        FileBaseService service = null;
        if (!srcFile.exists()) {
            throw new FileNotFoundException(srcFile.getPath() + "");
        }
        try {
            Map<String,FileBaseService> serviceMap = new HashMap<>();
            session = MybatisConnect.getSession();
            ZipFile zipFile = new ZipFile(srcFile);
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String dirPath = StringUtils.filePathDeal(destDirPath + "/" + entry.getName());
                if (entry.isDirectory()) {
                    new File(getAbsolutePath(dirPath)).mkdir();
                    srcFile.mkdirs();
                } else {
                    service = serviceMap.get(dirPath);
                    if(service == null){
                        service = FileBaseService.getInstance(dirPath,this.user,AbsoluteNetdiskDirectory.default_priviledge);
                        serviceMap.put(destDirPath,service);
                    }
                    String fileName = entry.getName().substring(entry.getName().lastIndexOf("/")+1);
                    service.getNetdiskFile().setFile_Name(fileName);
                    try {
                        service.uploadFile(zipFile.getInputStream(entry));
                    } catch (RuntimeException e) {
                        //文件已经存在
                    }
                }
            }
            session.commit();
        } finally {
            close(session);
        }
    }

/*    public void deCompress()throws Exception{
        deCompress(this.file,netdiskFile.getFile_Path().substring(0,netdiskFile.getFile_Path().lastIndexOf("/")));
    }*/
    public void compression() throws Exception {
        String path = netdiskFile.getFile_Path().substring(0,netdiskFile.getFile_Path().lastIndexOf("/"));
        compress(this.file,new ZipOutputStream(new FileOutputStream(path)),netdiskFile.getFile_Name());
    }

    public AbsoluteNetdiskFile getNetdiskFile() {
        return netdiskFile;
    }

    public void setNetdiskFile(AbsoluteNetdiskFile netdiskFile) {
        this.netdiskFile = netdiskFile;
    }

    public User getUser() {
        return user;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AbsoluteNetdiskEntity getNetdiskEntity() {
        return netdiskEntity;
    }

    public void setNetdiskEntity(AbsoluteNetdiskEntity netdiskEntity) {
        this.netdiskEntity = netdiskEntity;
    }

    public AbsoluteNetdiskDirectory getNetdiskDirectory() {
        return netdiskDirectory;
    }

    public void setNetdiskDirectory(AbsoluteNetdiskDirectory netdiskDirectory) {
        this.netdiskDirectory = netdiskDirectory;
    }

    @Override
    public String toString() {
        return "FileServiceImpl{" +
                "user=" + user +
                ", file=" + file +
                ", netdiskFile=" + netdiskFile +
                ", file_length=" + file_length +
                ", isMapper=" + isMapper +
                '}';
    }
}
