package com.virtualpuffer.netdisk.service.impl;


import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.MybatisConnect;
import com.virtualpuffer.netdisk.data.FileCollection;
import com.virtualpuffer.netdisk.entity.FileHash_Map;
import com.virtualpuffer.netdisk.entity.File_Map;
import com.virtualpuffer.netdisk.entity.NetdiskFile;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.utils.Message;
import com.virtualpuffer.netdisk.utils.StringUtils;
import com.virtualpuffer.netdisk.utils.TestTime;
import org.apache.ibatis.session.SqlSession;
import com.virtualpuffer.netdisk.mapper.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.*;
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
@Service
public class FileServiceImpl extends FileServiceUtil{
    private User user;
    private File file;
    private NetdiskFile netdiskFile;
    private int file_length;
    private boolean isMapper = false;
    private static final int BUFFER_SIZE = 4 * 1024;
    public static final String downloadAPI = Message.getMess("downloadAPI");//下载链接前缀
    public static final String defaultWare = Message.getMess("defaultWare");
    public static final String duplicateFileWare = Message.getMess("duplicateFileWare");
    public FileServiceImpl(){}

    /**
    * 构造服务对象
     * @param netdiskFile 目标文件对象
     * @param user 用户对象
     * 没有处理404情况，controller级别再获取
    * */
    public FileServiceImpl(NetdiskFile netdiskFile,User user) throws FileNotFoundException {
        this.user = user;
        this.netdiskFile = netdiskFile;
        this.file = netdiskFile.getFile();
    }

    public static FileServiceImpl getInstanceByPath(String path,int userID) throws FileNotFoundException{
        SqlSession session = MybatisConnect.getSession();
        User user = session.getMapper(UserMap.class).getUserByID(userID);
        NetdiskFile netdiskFile = new NetdiskFile(path);
        return new FileServiceImpl(netdiskFile,user);
    }
    public static FileServiceImpl getInstance(String destination,int userID) throws FileNotFoundException{
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
            NetdiskFile netdiskFile = NetdiskFile.getInstance(destination,userID);
            return new FileServiceImpl(netdiskFile,user);
        } finally {
            close(session);
        }
    }

    public static FileServiceImpl getInstanceByHash(String hash,String name) throws FileNotFoundException {
            FileServiceImpl impl = new FileServiceImpl();
            NetdiskFile netdiskFile = NetdiskFile.getInstance(hash,name);
            impl.setNetdiskFile(netdiskFile);
            impl.setFile(netdiskFile.getFile());
            return impl;
    }
    /**
     * @param token 需要解析的token
     * 解析token里的FileService对象
     *
     * 没办法确定是hash还是path(不知道是不是文件)
     * 文件夹没办法给hash，只能给路径，被删了就没办法了
    * */
    public static FileServiceImpl getInstanceByToken(String token) throws FileNotFoundException {
        Map map = parseJWT(token);
        if(map.get("hash") == null){
            String path = (String) map.get("path");
            int id = (Integer) map.get("userID");
            return getInstanceByPath(path,id);
        }else {
            String hash = (String) map.get("hash");
            String name = (String) map.get("name");
            return getInstanceByHash(hash,name);
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
     * 是文件时给hash(防止文件位置变动)
     * 文件夹时给path(没办法了，不然得做数据库维护映射，太难了)
     *
     * 映射型文件是系统没办法判断类型的（因为不存在）
    * */
    public String getDownloadURL(long time,@Nullable String key) throws Exception {
        Map<String,Object> map = new HashMap();
        if (this.file.isDirectory()) {
            map.put("path",netdiskFile.getFile_Path());
        }else {
            map.put("hash",getSH256(this.file));
        }
        map.put("userID",this.user.getUSER_ID());
        map.put("name",this.netdiskFile.getFile_Name());
        return  downloadAPI + createToken(time,map,user.getUsername(),key);
    }


    /**
     * 获取路径下文件
     * */
    public Map getDirectory() throws NoSuchFileException {
        Map ret = new HashMap();
        ArrayList filelist = new ArrayList();
        SqlSession session = MybatisConnect.getSession();
        ArrayList<String> dirList = new ArrayList();

        LinkedList<File_Map> list = session.getMapper(FileMap.class).getDirectoryMap(netdiskFile.getFile_Destination(),user.getUSER_ID());

        ret.put("file",filelist);
        ret.put("dir",dirList);

        if(!list.isEmpty()){
            for(File_Map fileMap : list){
                if(!fileMap.getFile_Destination().substring(netdiskFile.getFile_Destination().length() + 1).contains("/")){
                    filelist.add(fileMap.getFile_Destination().substring(fileMap.getFile_Destination().lastIndexOf("/")+1));
                }
            }
        }
        if(!file.isDirectory()&&dirList.isEmpty()){
            throw new NoSuchFileException("不是文件夹");
        }

        if(file.isDirectory()){
            for (File dirFile : file.listFiles()){
                if(dirFile.isFile()){
                    filelist.add(dirFile.getName());
                }else if(dirFile.isDirectory()){
                    dirList.add(dirFile.getName());
                }
            }
        }
        return ret;
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
        if(this.file.isDirectory()){
            zipOutputStream = new ZipOutputStream(outputStream);
            compress(this.file,zipOutputStream,netdiskFile.getFile_Name());
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
            close(zipOutputStream);
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
    * @param input 上传文件的输入流
    * */
    public void uploadFile(InputStream input)throws Exception{
        OutputStream outputStream;
        InputStream[] inputStreams = copyStream(input);
        InputStream inputStream = inputStreams[1];

        String hash = getSH256(inputStreams[0]);
        SqlSession session = MybatisConnect.getSession();

        dumplicateParse(hash);
        try {
            if (checkDuplicate(hash)) {
                //看看有没有创建映射,
          /*      if (session.getMapper(FileMap.class).invokeOnExit(hash).isEmpty()) {
                    //查询文件当前位置
                    FileHash_Map map = session.getMapper(FileHashMap.class).getFilePath(hash);
                    String file_path = map.getPath();
                    int id = map.getUSER_ID();
                    String hashFile_path = duplicateFileWare + hash;
                    //复制
                    copy(new FileInputStream(file_path),new FileOutputStream(hashFile_path));

                    //更新hash表路径
                    session.getMapper(FileHashMap.class).updatePath(hash,hashFile_path);

                    new File(file_path).delete();
                    //源文件映射建立
                    FileServiceImpl original = getInstanceByPath(file_path,id);//操作对象
                    NetdiskFile file = original.getNetdiskFile();
                    session.getMapper(FileMap.class).buildFileMap(file.getFile_Destination(),file.getFile_Name(),hash,original.getUser().getUSER_ID());
                }*/
                session.getMapper(FileMap.class).buildFileMap(this.netdiskFile.getFile_Destination(),this.file.getName(),hash,this.user.getUSER_ID());
                session.commit();
            }else {
                String hashFile_path = duplicateFileWare + hash;//map里面的位置
                outputStream = new FileOutputStream(hashFile_path);
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
                    session.getMapper(FileMap.class)
                            .buildFileMap(this.netdiskFile.getFile_Destination(),this.file.getName(),hash,this.user.getUSER_ID());
                    session.getMapper(FileHashMap.class).addHashMap(hash,hashFile_path,user.getUSER_ID());
                    session.commit();
                }
            }
        } finally {
            close(inputStream);
            close(session);
        }
    }
    //上面的工具类，有重复文件时在后缀前加上（1），如果已经存在就递增
    public void dumplicateParse(String hash) throws Exception {
        File on = new File(this.netdiskFile.getFile_Path());
        //重名文件处理
        if(on.exists()){
            if(hash.equals(getSH256(on))){
                throw new RuntimeException("file has been exit");
            }else {
                String path = this.netdiskFile.getFile_Path();
                //重复的话加上数字
                String destination = StringUtils.duplicateRename(this.netdiskFile.getFile_Destination());
                this.netdiskFile = NetdiskFile.getInstance(destination,this.user.getUSER_ID());
                dumplicateParse(hash);
            }
        }
        return;
    }

    /**
     * 文件删除
     * 检查映射表，存在则删除
     * 映射表不存在，检查
     * */
    public void deleteFileMap(){
        SqlSession session = null;
        try {
            if(file.exists()){
                delete(file);
            }
            session = MybatisConnect.getSession();
            FileMap fileMap = session.getMapper(FileMap.class);
            int count = fileMap.deleteFileMap(netdiskFile.getFile_Destination(),user.getUSER_ID());
            /*     count += fileMap.deleteDirectoryMap(destination + "/",user.getUSER_ID());*/
            if(count > 0){
                session.commit();
                return ;
            }
            delete(this.file);
            return ;
        } finally {
            close(session);
        }
    }

    /**
     * 是文件则后序遍历删除，否则直接删除
     * */
    private static void delete(File on){
        if(on.getParentFile().equals(new File(duplicateFileWare))){
            return;//别删仓库的
        }
        if(on.isDirectory()){
            File[] get = on.listFiles();
            if(get.length!=0){
                for(File a : get){
                    delete(a);
                }
            }
            on.delete();
        }else {
            on.delete();
        }
    }

    public long count() throws RuntimeException {
        ZipOutputStream zos = null;
        String fileName = this.netdiskFile.getFile_Name();
        String path = getMess("compressTemp") + fileName.hashCode();
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
            } catch (Exception e) {
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
        try {
            session = MybatisConnect.getSession();
            FileCollection collection = FileCollection.getInstance(this.file,name,getAbsolutePath("/"),type);
            LinkedList fileList = collection.getFile();
            LinkedList dirList = collection.getDir();
            LinkedList<File_Map> list = session.getMapper(FileMap.class).getDirectoryMap(netdiskFile.getFile_Destination(),user.getUSER_ID());
            if(!list.isEmpty()){
                for(File_Map fileMap : list){
                    try {
                        if(!fileMap.getFile_Destination().substring(fileMap.getFile_Destination().lastIndexOf("/" + 1)).equals(name)){
                            fileList.add(fileMap.getFile_Destination());
                        }
                    } catch (Exception e) {//防止爆掉
                    }
                }
            }
            if(fileList.isEmpty()&&dirList.isEmpty()){
                collection.setMsg("没有找到匹配的文件");
                collection.setCode(300);
            }else {
                collection.setMsg("已找到匹配的文件");
                collection.setCode(200);
            }
            return collection;
        } finally {
            close(session);
        }
    }
    public void mkdir() throws RuntimeException{
        if(this.file.exists()){
            throw new RuntimeException("同名文件或文件夹已经存在");
        }else {
            this.file.mkdir();
        }
    }

    public void rename(String name) throws Exception {
        if(!this.file.exists()){
            throw new FileNotFoundException("重命名目标不存在");
        }
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            String destination = this.netdiskFile.getFile_Destination()
                    .substring(0,this.netdiskFile.getFile_Destination().lastIndexOf("/")+1) + name;

            NetdiskFile netdiskFile = NetdiskFile.getInstance(destination,this.user.getUSER_ID());
            //看看有没有重名的
            if (netdiskFile.getFile().exists()) {
                throw new RuntimeException("重复文件名扔上来搞毛？");
            }
            int ret = session.getMapper(FileMap.class).renameFile(
                    this.netdiskFile.getFile_Destination(),this.user.getUSER_ID(),name,destination);
            if(ret == 1){
                session.commit();
                return;
            }else {
                session.rollback();
            }

            int count = session.getMapper(FileHashMap.class)
                    .updatePath(this.netdiskFile.getFile_Hash(),getAbsolutePath(destination));
            File now = netdiskFile.getFile();
            System.out.println(netdiskFile);
            this.file.renameTo(now);
            if(count == 1){
                session.commit();
            }
        } finally {
            close(session);
        }
    }

    public void deCompress()throws Exception{
        deCompress(this.file,netdiskFile.getFile_Path().substring(0,netdiskFile.getFile_Path().lastIndexOf("/")));
    }
    public void compression() throws Exception {
        String path = netdiskFile.getFile_Path().substring(0,netdiskFile.getFile_Path().lastIndexOf("/"));
        compress(this.file,new ZipOutputStream(new FileOutputStream(path)),netdiskFile.getFile_Name());
    }

    public NetdiskFile getNetdiskFile() {
        return netdiskFile;
    }

    public void setNetdiskFile(NetdiskFile netdiskFile) {
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
