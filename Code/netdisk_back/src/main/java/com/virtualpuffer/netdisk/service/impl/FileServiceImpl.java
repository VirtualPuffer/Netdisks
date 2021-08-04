package com.virtualpuffer.netdisk.service.impl;


import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.MybatisConnect;
import com.virtualpuffer.netdisk.entity.FileHash_Map;
import com.virtualpuffer.netdisk.entity.File_Map;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.utils.Message;
import org.apache.ibatis.session.SqlSession;
import com.virtualpuffer.netdisk.mapper.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
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
    private String path;//绝对路径
    private String destination;//相对路径
    private static final int BUFFER_SIZE = 4 * 1024;
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
        this.destination = destination;
        this.file = new File(getAbsolutePath(destination));

        try {
            this.path = file.getCanonicalPath();
        } catch (IOException e) {
            this.path = file.getAbsolutePath();
        }
        //长度判断，过短说明跳到上级路径
        if(this.path.length() < defaultWare.length() && this.path.length() < duplicateFileWare.length()){
            throw new SecurityException();
        }
    }
    public static FileServiceImpl getInstanceByPath(String path,String userID) throws FileNotFoundException{
        SqlSession session = MybatisConnect.getSession();
        User user = session.getMapper(UserMap.class).getUserByID(userID).getFirst();
        String destination = path.substring(defaultWare.length() + user.getURL().length());
        return new FileServiceImpl(destination,user);
    }
    public static FileServiceImpl getInstance(String destination,String userID) throws FileNotFoundException{
        SqlSession session = MybatisConnect.getSession();
        User user = session.getMapper(UserMap.class).getUserByID(userID).getFirst();
        return new FileServiceImpl(destination,user);
    }
    /**
    * 物理路径计算
     * @param destination 相对路径位置（网盘）
    * */
    public String getAbsolutePath(String destination){
        return defaultWare + this.user.getURL() + destination;
    }
    /**
     * 获取路径下文件
     * */
    public ArrayList<String> getDirectory(){
        SqlSession session = MybatisConnect.getSession();
        ArrayList<String> arrayList = new ArrayList();
        LinkedList<File_Map> list = session.getMapper(FileMap.class).getDirectoryMap(destination,user.getUSER_ID());
        if(!list.isEmpty()){
            for(File_Map fileMap : list){
                arrayList.add(fileMap.getFile_Destination());
            }
        }
        if(file.isDirectory()){
            for (File dirFile : file.listFiles()){
                arrayList.add(dirFile.getName());
            }
        }
        return arrayList;
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
        SqlSession session = MybatisConnect.getSession();
        LinkedList list = session.getMapper(FileHashMap.class).checkDuplicate(hash);
        return !list.isEmpty();
    }
    /*public boolean duplicateUpload(String hash,String)*/
    public void uploadFile(InputStream inputStream)throws Exception{
        OutputStream outputStream;
        String hash = getSH256(inputStream);
        SqlSession session = MybatisConnect.getSession();
        if (checkDuplicate(inputStream)) {
            //看看有没有创建映射,
            if (session.getMapper(FileMap.class).invokeOnExit(hash).isEmpty()) {
                //查询文件当前位置
                LinkedList<FileHash_Map> list = session.getMapper(FileHashMap.class).getFilePath(hash);
                String file_path = list.getFirst().getPath();
                String hashFile_path = duplicateFileWare;
                //更新hash表路径
                session.getMapper(FileHashMap.class).updatePath(hash,hashFile_path);
                //复制
                copy(new FileInputStream(file_path),new FileOutputStream(hashFile_path));
                new File(file_path).delete();
                //源文件映射建立
                getInstanceByPath("",file_path);//操作对象
                session.getMapper(FileMap.class).buildFileMap(file_path,"name",hash);

            }
            session.getMapper(FileMap.class).insertMap(user.getUSER_ID(),hash,file.getName());

        }else {

            session.getMapper(FileHashMap.class).addHashMap(hash,path,user.getUSER_ID());
            outputStream = new FileOutputStream(path);
            try {
                copy(inputStream,outputStream);
            } catch (IOException e) {
                //日志工厂
            } finally {
                close(inputStream);
                close(outputStream);
            }

            session.commit();/*提交*/
        }
    }

    /**
     * 文件删除
     * 检查映射表，存在则删除
     * 映射表不存在，检查
     * */
    public boolean deleteFileMap(){
        if(file.exists()){
            delete(file);
        };
        SqlSession session = MybatisConnect.getSession();
        FileMap fileMap = session.getMapper(FileMap.class);

        fileMap.deleteFileMap(destination,user.getUSER_ID());
        fileMap.deleteDirectoryMap(destination + "/",user.getUSER_ID());

        session.commit();
        session.close();
        return false;
    }

    /**
     * 是文件则后序遍历删除，否则直接删除
     * */
    public static void delete(File on){
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

    public static long count(String srcDir) throws RuntimeException {
        ZipOutputStream zos = null;
        String fileName = srcDir.substring(srcDir.lastIndexOf("/"));
        String path = getMess("compressTemp") + fileName.hashCode();
        File a = new File(path);
        try {
            a.createNewFile();
        } catch (IOException e) {
            System.out.println("没出来？");
        }
        try {
            OutputStream out = new FileOutputStream(path);
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName());
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            close(zos);
        }
        File temp = new File(path);
        long ret = temp.length();
        temp.delete();
        return ret;
    }
}
