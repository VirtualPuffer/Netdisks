package com.virtualpuffer.netdisk.mapper;


import java.util.LinkedList;

public interface FileMap {
    LinkedList userFile();
    /*
    * 删除文件
    * */
    //只删除对应路径
    int deleteFileMap(String path);
    //正则匹配前缀
    int deleteDirectoryMap(String path);
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
    * 搜索文件
    * */
    LinkedList searchFile(String name);
    /**
     * 上传文件
     * */
    LinkedList checkDuplicate(String hash);
    int insertMap(String userID,String fileHash);


}
