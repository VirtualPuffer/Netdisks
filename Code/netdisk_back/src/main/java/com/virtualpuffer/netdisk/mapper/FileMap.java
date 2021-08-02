package com.virtualpuffer.netdisk.mapper;


import java.util.LinkedList;

public interface FileMap {
    LinkedList userFile();
    /*
    * 删除文件
    * */
    //只删除对应路径
    int deleteFileMap(String path,int userID);
    //正则匹配前缀
    int deleteDirectoryMap(String path,int userID);
    //检查还有没有其他文件引用
    LinkedList invokeOnExit(String path);
    /*
    * 添加文件映射：
    * 1.检查父级路径是否存在
    * 2.存在则添加，否则不添加
    * */
    LinkedList parentONExit(String path);
    int addFile(String destination,String name);
    /*
    * 获取文件路径映射
    * */
    LinkedList getDirectoryMap(String destination,int userID);
    /*
    * 搜索文件
    * */
    LinkedList searchFile(String fileName,int userID);
    /**
     * 上传文件
     * */
    LinkedList checkDuplicate(String hash);
    int insertMap(String userID,String fileHash,String fileName);


}
