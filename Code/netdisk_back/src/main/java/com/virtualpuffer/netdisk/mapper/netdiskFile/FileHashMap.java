package com.virtualpuffer.netdisk.mapper.netdiskFile;

import com.virtualpuffer.netdisk.entity.file.FileHash_Map;
import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskFile;

import java.util.LinkedList;

public interface FileHashMap {
    /**
     * 上传文件
     * */
    LinkedList checkDuplicate(String hash);

    FileHash_Map getFilePath(String hash);

    int updatePath(String fileHash,String filePath);

    int addHashMap(String hash,String path,int userID,int length);

    AbsoluteNetdiskFile getFileMapByHash(String hash);

    FileHash_Map getFileMapByPath(String path);

    AbsoluteNetdiskFile getPath(String hash);

    int updateDestination(String destination, String newDestination);

}
