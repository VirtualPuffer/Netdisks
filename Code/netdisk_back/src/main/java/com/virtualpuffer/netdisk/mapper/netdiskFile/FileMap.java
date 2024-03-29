package com.virtualpuffer.netdisk.mapper.netdiskFile;



import com.virtualpuffer.netdisk.entity.file.File_Map;
import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskFile;

import java.util.HashSet;
import java.util.LinkedList;

public interface FileMap {
    HashSet<AbsoluteNetdiskFile> getChildrenFileID(int Directory_ID);

    AbsoluteNetdiskFile getFileByMapID(int Map_id);

    AbsoluteNetdiskFile fileOnExits(int USER_ID,int Directory_Parent_ID,String fileName);

    int rename(int USER_ID,int Directory_Parent_ID,int Map_id,String fileName);

    LinkedList<String> getDir(int USER_ID,int Directory_ID);
    //只删除对应路径
    int deleteFileMap(int Map_id,int userID);
    //正则匹配前缀
    int deleteDirectoryMap(String path,int userID);
    //检查是否存在映射
    LinkedList<File_Map> invokeOnExit(String hash);
    int buildFileMap(String fileName,String hash,int userID,int Directory_Parent_Place);
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
    LinkedList searchFile(String name,int userID,int priviledge);
    /**
     * 上传文件
     * */

    //AbsoluteNetdiskFile getFileMap(int userID, String destination);

    AbsoluteNetdiskFile getFileMap(int userID,int Parent_Directory_ID ,String fileName);

    File_Map getFileMapByPath(String path,int userID);

    /*
    * 重命名文件
    * */
    int renameFile(String destination,int userID,String name,String newDestination);

    LinkedList<String> gc();


}
