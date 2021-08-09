package com.virtualpuffer.netdisk.mapper;

import com.virtualpuffer.netdisk.entity.FileHash_Map;
import com.virtualpuffer.netdisk.entity.File_Map;

import java.util.LinkedList;

public interface FileHashMap {
    /**
     * 上传文件
     * */
    LinkedList checkDuplicate(String hash);

    LinkedList getFilePath(String hash);

    int updatePath(String fileHash,String filePath);

    int addHashMap(String hash,String path,int userID);

    FileHash_Map getFileMapByHash(String hash);

    FileHash_Map getFileMapByPath(String path);

}
