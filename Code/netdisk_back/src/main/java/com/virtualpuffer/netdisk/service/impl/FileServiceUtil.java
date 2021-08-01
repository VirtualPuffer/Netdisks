package com.virtualpuffer.netdisk.service.impl;

import java.io.*;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileServiceUtil extends BaseServiceImpl{

    /**
     * 源文件获取hash值
     * 把文件转化为输入流
     * 通过MessageDigest获取散列示例
     * @param file  待计算文件
     * @param1 type  加密类型
     */
    protected static String getSH256(File file)throws Exception{
        return getHash(file,"SHA-256");
    }
    protected static String getSH256(InputStream inputStream)throws Exception{
        return getHash(inputStream,"SHA-256");
    }
    protected static String getHash(File file,String type)throws Exception{
        InputStream inputStream = new FileInputStream(file);
        return getHash(inputStream,type);
    }
    protected static String getHash(InputStream inputStream, String type)throws Exception{
        MessageDigest instance = MessageDigest.getInstance(type);
        byte buffer[] = new byte[1024];
        for (int numRead = 0; (numRead = inputStream.read(buffer)) > 0; ) {
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
     * 递归压缩方法
     * @param sourceFile 源文件
     * @param zos        zip输出流
     * @param name       压缩后的名称
     * @throws Exception
     */
    protected static int compress(File sourceFile, ZipOutputStream zos, String name) throws Exception {
        int size = 0;
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
                        size = size + compress(file, zos, name + "/" + file.getName());
                }
            }
        }
        return size;
    }
    /**
     * 源文件获取zip键值对文件集合
     * 用Getname获取路径
     * 没有则创建路径
     * zip文件解压
     * @param inputFile  待解压文件夹/文件
     * @param destDirPath  解压路径
     */
    protected static void deCompress(String inputFile,String destDirPath) throws FileNotFoundException, IOException {
        File file = new File(inputFile);
        deCompress(file,destDirPath);
    }
    protected static void deCompress(File srcFile,String destDirPath) throws FileNotFoundException,IOException {
        if (!srcFile.exists()) {
            throw new FileNotFoundException(srcFile.getPath() + "");
        }
        ZipFile zipFile = new ZipFile(srcFile);
        Enumeration<?> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            // 如果是文件夹，就创建个文件夹
            if (entry.isDirectory()) {
                String dirPath = destDirPath + "/" + entry.getName();
                srcFile.mkdirs();
            } else {
                // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                File targetFile = new File(destDirPath + "/" + entry.getName());
                // 保证这个文件的父文件夹必须要存在
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs();
                }
                targetFile.createNewFile();
                InputStream is = zipFile.getInputStream(entry);
                FileOutputStream target = new FileOutputStream(targetFile);
                int arrayLength;
                byte[] buf = new byte[BUFFER_SIZE];
                while ((arrayLength = is.read(buf)) != -1) {
                    target.write(buf,0,arrayLength);
                }
                target.close();
                is.close();
            }
        }
    }
}

