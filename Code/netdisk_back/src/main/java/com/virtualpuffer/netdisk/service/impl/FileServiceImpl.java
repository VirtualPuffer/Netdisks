package com.virtualpuffer.netdisk.service.impl;


import com.virtualpuffer.netdisk.MybatisConnect;
import com.virtualpuffer.netdisk.entity.User;
import org.apache.ibatis.session.SqlSession;
import com.virtualpuffer.netdisk.mapper.*;
import java.io.*;
import java.util.zip.ZipOutputStream;

public class FileServiceImpl extends FileServiceUtil{
    private User user;
    private String USER_ID;
    private File file;
    private String path;
    public FileServiceImpl(){}

    /**
    * 构造服务对象
    * */
    public FileServiceImpl(String path) throws FileNotFoundException {
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
    public FileServiceImpl(File file) throws FileNotFoundException{
        this.file = file;
        if(!this.file.exists()) {
            throw new FileNotFoundException("");
        }
        try {
            this.path = file.getCanonicalPath();
        } catch (IOException e) {
            this.path = file.getAbsolutePath();
        }
    }

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
