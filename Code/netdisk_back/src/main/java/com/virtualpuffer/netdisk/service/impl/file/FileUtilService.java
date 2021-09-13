package com.virtualpuffer.netdisk.service.impl.file;

import com.virtualpuffer.netdisk.entity.AbsoluteNetdiskFile;
import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;
import org.apache.catalina.connector.CoyoteInputStream;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Async
@Service
public abstract class FileUtilService extends BaseServiceImpl {

    private static final int BUFFER_SIZE = 4 * 1024;

    public abstract void uploadFile(InputStream input) throws Exception;

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


    /**
    * 文件、文件流复制
    * */
    protected static void copy(InputStream inputStream,OutputStream outputStream)throws IOException{
            byte[] buffer = new byte[BUFFER_SIZE];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
    }

    protected static void readCopy(FileInputStream inputStream,OutputStream outputStream){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
    }

    protected static void writeCopy(){}

    /**
     * 获取映射文件并放入压缩集合中
     *
     * */
    protected static ZipOutputStream compress(File sourceFile,ZipOutputStream outputStream, LinkedList<AbsoluteNetdiskFile> list){
        byte[] buf = new byte[BUFFER_SIZE];
        Iterator<AbsoluteNetdiskFile> iterator = list.iterator();
        while (iterator.hasNext()){
            try {
                AbsoluteNetdiskFile file = iterator.next().handleInstance();
                outputStream.putNextEntry(new ZipEntry(file.getFile_Destination()));
                FileInputStream inputStream = new FileInputStream(file.getFile_Path());
                int length;
                while ((length = inputStream.read(buf)) != -1){
                    outputStream.write(buf,0,length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputStream;
    }
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

