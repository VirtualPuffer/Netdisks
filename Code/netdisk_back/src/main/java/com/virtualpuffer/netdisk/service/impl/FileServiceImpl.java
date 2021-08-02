package com.virtualpuffer.netdisk.service.impl;


import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.MybatisConnect;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.utils.Message;
import org.apache.ibatis.session.SqlSession;
import com.virtualpuffer.netdisk.mapper.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.LinkedList;
import java.util.zip.ZipOutputStream;


/**
* 只处理文件操作，不涉及用户信息
* */
@Service
public class FileServiceImpl extends FileServiceUtil{
    private User user;
    private File file;
    private String path;
    private static final int BUFFER_SIZE = 4 * 1024;
    public static final String defaultWare = Message.getMess("defaultWare");
    public FileServiceImpl(){}

    /**
    * 构造服务对象
    * */
    public FileServiceImpl(String path,User user) throws FileNotFoundException {
        this.user = user;
        this.file = new File(path);
        if(this.file.exists()) {
            throw new FileNotFoundException("");
        }
        try {
            this.path = file.getCanonicalPath();
        } catch (IOException e) {
            this.path = file.getAbsolutePath();
        }
    }
    public FileServiceImpl(File file,User user) throws FileNotFoundException{
        this.file = file;
        this.user = user;
        if(!this.file.exists()) {
            throw new FileNotFoundException("");
        }
        try {
            this.path = file.getCanonicalPath();
        } catch (IOException e) {
            this.path = file.getAbsolutePath();
        }
    }
    /**
    * 物理路径计算
    * */
    public String getAbsolutePath(String destination){
        return defaultWare + this.user.getURL() + destination;
    }
    /**
     * 获取路径下文件
     * */
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
    public boolean uploadFile(InputStream inputStream,String destination)throws Exception{
        OutputStream outputStream;
        if (checkDuplicate(inputStream)) {
            SqlSession session = MybatisConnect.getSession();
            int count = session.getMapper(FileMap.class).insertMap(user.getURL(),getSH256(inputStream));
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
        }
        return false;
    }
    /**
     * 文件删除
     * */
    public boolean deleteFileMap(String path){
        //删除文件映射
        SqlSession session = MybatisConnect.getSession();
        FileMap fileMap = session.getMapper(FileMap.class);
        fileMap.deleteFileMap(path);
        if(fileMap.invokeOnExit(path).isEmpty()){
            //删除物理文件
            deleteFile();
        };
        session.commit();
        session.close();
        return false;
    }
    public boolean deleteFile() {
        try {
            //删除操作
            delete(this.file);
        } catch (Exception e) {
            if(file.exists()){
                //删除失败
                return false;
            }
        }
        //清除map
        return true;
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
