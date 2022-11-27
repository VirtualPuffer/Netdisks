package com.virtualpuffer.netdisk.service.impl.file;

import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskDirectory;
import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskEntity;
import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskFile;
import com.virtualpuffer.netdisk.mapper.netdiskFile.DirectoryMap;
import com.virtualpuffer.netdisk.mapper.netdiskFile.FileMap;
import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;
import com.virtualpuffer.netdisk.utils.MybatisConnect;
import org.apache.ibatis.session.SqlSession;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//@Async
@Service
public abstract class FileUtilService extends BaseServiceImpl {

    private static final int BUFFER_SIZE = 4 * 1024;

    public abstract void uploadFile(InputStream input) throws Exception;

    public static InputStream getStringInputStream(String s) {
        if (s != null && !s.equals("")) {
            try {
                ByteArrayInputStream stringInputStream = new ByteArrayInputStream(s.getBytes());
                return stringInputStream;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Reader getStringReader(String s) {
        if (s != null && !s.equals("")) {
            try {
                StringReader stringReader = new StringReader(s);
                return stringReader;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static String getSH256(String string)throws Exception{
        return getSH256(getStringInputStream(string));
    }
    /**
     * 源文件获取hash值
     * 把文件转化为输入流
     * 通过MessageDigest获取散列示例
     * @param file  待计算文件
     * @param1 type  加密类型
     */
    public static String getSH256(File file)throws Exception{
        return getHash(file,"SHA-256");
    }
    protected static String getSH256(InputStream inputStream)throws Exception{
        return getHash(inputStream,"SHA-256");
    }
    protected static String getHash(File file,String type)throws Exception{
        InputStream inputStream = new FileInputStream(file);
        return getHash(inputStream,type);
    }
    protected static String getHash(InputStream input, String type)throws Exception{
        MessageDigest instance = MessageDigest.getInstance(type);
        byte buffer[] = new byte[1024];
        for (int numRead = 0; (numRead = input.read(buffer)) > 0; ) {
            instance.update(buffer, 0, numRead);
        }
        return toHexString(instance.digest());
    }
    protected static String toHexString(byte b[]) {
        StringBuilder sb = new StringBuilder();
        for (byte aB : b) {
            sb.append(Integer.toHexString(aB & 0xFF));
        }
        return sb.toString();
    }

    protected static void readCopy(FileInputStream inputStream,OutputStream outputStream){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
    }

    protected static void writeCopy(){}


    /**
     * 关闭Zip流时会把输出流也同时关闭，慎用！
     * 递归压缩方法
     * @param sourceFile 源文件
     * @param zos        zip输出流
     * @param name       压缩后的名称
     * @throws Exception
     */
    protected static void compress(File sourceFile, ZipOutputStream zos, String name) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            zos.putNextEntry(new ZipEntry(name));
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    zos.closeEntry();
            } else {
                for (File file : listFiles) {
                    compress(file, zos, name + "/" + file.getName());
                }
            }
        }
    }

}

