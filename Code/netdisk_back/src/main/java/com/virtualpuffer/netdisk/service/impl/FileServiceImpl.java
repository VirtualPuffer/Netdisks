package com.virtualpuffer.netdisk.service.impl;


import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.MybatisConnect;
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
 * 则不再上传文件，只在映射表中添加映射
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
    public FileServiceImpl(){}

    /**
    * 构造服务对象
     * @param destination 相对文件路径
     * @param user 用户对象
     * 没有处理404，controller级别再获取
    * */
    public FileServiceImpl(String destination,User user) throws FileNotFoundException {
        this.user = user;
        this.destination = destination;
        this.file = new File(getAbsolutePath(destination));
 /*       if(this.file.exists()) {
            throw new FileNotFoundException("");
        }*/
        try {
            this.path = file.getCanonicalPath();
        } catch (IOException e) {
            this.path = file.getAbsolutePath();
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
     * 获取路径下文件
     * */
    public ArrayList<String> getDirectory(){
        SqlSession session = MybatisConnect.getSession();
        ArrayList<String> arrayList = new ArrayList();
        LinkedList<File_Map> list = session.getMapper(FileMap.class).getDirectoryMap(destination,user.getUSER_ID());
        if(!list.isEmpty()){
            for(File_Map fileMap : list){
                arrayList.add(fileMap.getFile_Path());
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
     * */
    //SHA校验
    public boolean checkDuplicate(InputStream inputStream) throws Exception{
        return checkDuplicate(getSH256(inputStream));
    }
    //重复返回true
    public boolean checkDuplicate(String hash){
        SqlSession session = MybatisConnect.getSession();
        LinkedList list = session.getMapper(FileMap.class).checkDuplicate(hash);
        return !list.isEmpty();
    }
    /*public boolean duplicateUpload(String hash,String)*/
    public boolean uploadFile(InputStream inputStream)throws Exception{
        OutputStream outputStream;
        SqlSession session = MybatisConnect.getSession();
        if (checkDuplicate(inputStream)) {
            int count = session.getMapper(FileMap.class).insertMap(user.getURL(),getSH256(inputStream),file.getName());
        }else {
            byte[] buffer = new byte[BUFFER_SIZE];
            outputStream = new FileOutputStream(path);
            int length = 0;
            try {
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
            } catch (IOException e) {
                //日志工厂
            } finally {
                close(inputStream);
                close(outputStream);
            }
            session.commit();/*提交*/
        }
        return false;
    }
    /**
     * 文件删除
     * */
    @Deprecated
    public boolean deleteFileMap(){
        //删除文件映射
        SqlSession session = MybatisConnect.getSession();
        FileMap fileMap = session.getMapper(FileMap.class);
        fileMap.deleteFileMap(path,user.getUSER_ID());
        if(fileMap.invokeOnExit(path).isEmpty()){
            //删除物理文件
            deleteFile();
        };
        session.commit();
        session.close();
        return false;
    }
    public void deleteFile() {
        try {
            //删除操作
            delete(this.file);
        } catch (Exception e) {
            if(file.exists()){
                //删除失败
            }
        }
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
