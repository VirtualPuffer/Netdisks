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
    private String file_type1;
    private String file_name1;
    private String path1;//绝对路径
    private String destination1;//相对路径
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
     * @param destination 相对文件路径
     * @param user 用户对象
     * 没有处理404情况，controller级别再获取
    * */

    public FileServiceImpl(String destination,User user) throws FileNotFoundException {
        this.user = user;
        this.netdiskFile = NetdiskFile.getInstance(destination,user.getUSER_ID());
        /*SqlSession session = MybatisConnect.getSession();
        File_Map get = session.getMapper(FileMap.class).getFileMap(user.getUSER_ID(),destination);
        if(get == null){
            this.file = new File(getAbsolutePath(destination));
        }else {
            this.isMapper = true;
            FileHash_Map hashMap = session.getMapper(FileHashMap.class).getFileMapByHash(get.getFile_Hash());
            this.file = new File(netdiskFile.getFile_Path());
        }*/
    }
    public FileServiceImpl(User user,String path) throws FileNotFoundException {
        this.user = user;

        SqlSession session = MybatisConnect.getSession();
        FileHash_Map hashmap = session.getMapper(FileHashMap.class).getFileMapByPath(path);
        File_Map map = session.getMapper(FileMap.class).getFileMapByPath(path,user.getUSER_ID());

        if(map == null){
            this.file = new File(path);
            try {
                String tem = this.file.getCanonicalPath().substring(defaultWare.length());
                System.out.println(tem + "_____________________________>");
            } catch (IOException e) {

            }
        }else {
            this.isMapper = true;
            this.file = new File(netdiskFile.getFile_Path());
            //File拿真实路径回来
        }
    }

    public static FileServiceImpl getInstanceByPath(String path,int userID) throws FileNotFoundException{
        SqlSession session = MybatisConnect.getSession();
        User user = session.getMapper(UserMap.class).getUserByID(userID).getFirst();
        return new FileServiceImpl(user,StringUtils.filePathDeal(path));
    }
    public static FileServiceImpl getInstance(String destination,int userID) throws FileNotFoundException{
        if(destination == null){
            throw new FileNotFoundException("缺少参数:destination");
        }else if(!destination.startsWith("/")){//防止路径没/
            destination = new StringBuffer().append("/").append(destination).toString();
        }else if(destination.contains("..")){
            throw new RuntimeException("路径非法");
        }
        destination = StringUtils.filePathDeal(destination);
        SqlSession session = MybatisConnect.getSession();
        User user = session.getMapper(UserMap.class).getUserByID(userID).getFirst();
        return new FileServiceImpl(destination,user);
    }

    public static FileServiceImpl getInstanceByHash(String hash) throws FileNotFoundException {
        SqlSession session = null;
        try {
            FileHash_Map map = session.getMapper(FileHashMap.class).getFileMapByHash(hash);
            String path = map.getPath();
            int id = map.getUSER_ID();
            return getInstanceByPath(path,id);
        } finally {
            close(session);
        }
    }
    /**
     * @param token 需要解析的token
     * 解析token里的FileService对象
    * */
    public static FileServiceImpl getInstanceByToken(String token) throws FileNotFoundException {
        Map map = parseJWT(token);
        try {
            String hash = (String) map.get("hash");
            return getInstanceByHash(hash);
        } catch (RuntimeException e) {
            String path = (String) map.get("path");
            int id = (Integer) map.get("userID");
            return getInstanceByPath(path,id);
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
    * */
    public String getDownloadURL(long time,@Nullable String key) throws Exception {
        Map<String,Object> map = new HashMap();
        map.put("hash",getSH256(this.file));
        map.put("path",netdiskFile.getFile_Path());
        map.put("userID",this.user.getUSER_ID());
        return  downloadAPI + createToken(time,map,user.getUsername(),key);
    }


    /**
     * 获取路径下文件
     * */
    public Map getDirectory() throws NoSuchFileException {
        TestTime testTime = new TestTime();
        testTime.start();
        Map ret = new HashMap();
        ArrayList filelist = new ArrayList();
        SqlSession session = MybatisConnect.getSession();
        ArrayList<String> dirList = new ArrayList();

        LinkedList<File_Map> list = session.getMapper(FileMap.class).getDirectoryMap(netdiskFile.getFile_Destination().substring(1),user.getUSER_ID());

        ret.put("file",filelist);
        ret.put("dir",dirList);

        if(!list.isEmpty()){
            for(File_Map fileMap : list){
                if(!fileMap.getFile_Destination().substring(netdiskFile.getFile_Destination().length()).contains("/")){
                    filelist.add(fileMap.getFile_Destination());
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
        testTime.end();
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
                copy(inputStream,outputStream);
                return inputStream.available();
        }
        } finally {
            close(inputStream);
            close(zipOutputStream);
        }
    }
    /**
     * 操作流程：
     *  获取输入流
     *  计算sha256
     *  在file hashmap表中看看有没有已经存在的文件
     *  如果有，把该文件移到重复仓库中
     *  给源文件拥有者和上传者分别建立映射
     *  然后修改file hashmap路径使其指向当前文件位置
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
        try {
            if (checkDuplicate(hash)) {
                //看看有没有创建映射,
                if (session.getMapper(FileMap.class).invokeOnExit(hash).isEmpty()) {
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
                    session.getMapper(FileMap.class).buildFileMap(original.getDestination().substring(1),original.getFile_name(),hash,original.getUser().getUSER_ID());
                }
                session.getMapper(FileMap.class).buildFileMap(this.destination.substring(1),this.file.getName(),hash,this.user.getUSER_ID());
                session.commit();
            }else {
                session.getMapper(FileHashMap.class).addHashMap(hash,netdiskFile.getFile_Path(),user.getUSER_ID());
                outputStream = new FileOutputStream(netdiskFile.getFile_Path());
                try {
                    copy(inputStream,outputStream);
                    session.commit();
                } catch (IOException e) {
                    //日志工厂
                    e.printStackTrace();
                } finally {
                    close(outputStream);
                }
            }
        } finally {
            /*提交*/
            close(inputStream);
            close(session);
        }
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
            ;
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
    public void deCompress()throws Exception{
        deCompress(this.file,netdiskFile.getFile_Path().substring(0,netdiskFile.getFile_Path().lastIndexOf("/")));
    }
    public void compression() throws Exception {
        String path = netdiskFile.getFile_Path().substring(0,netdiskFile.getFile_Path().lastIndexOf("/"));
        compress(this.file,new ZipOutputStream(new FileOutputStream(path)),netdiskFile.getFile_Name());
    }


    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "FileServiceImpl{" +
                "user=" + user +
                ", file=" + file +
                ", file_type1='" + file_type1 + '\'' +
                ", file_name1='" + file_name1 + '\'' +
                ", path1='" + path1 + '\'' +
                ", destination1='" + destination1 + '\'' +
                ", netdiskFile=" + netdiskFile +
                ", file_length=" + file_length +
                ", isMapper=" + isMapper +
                '}';
    }
}
