package com.virtualpuffer.netdisk.mapper;

import com.virtualpuffer.netdisk.entity.FileHash_Map;
import com.virtualpuffer.netdisk.entity.File_Map;
import com.virtualpuffer.netdisk.entity.NetdiskFile;

import java.util.LinkedList;

public interface FileHashMap {
    /**
     * 上传文件
     * */
    LinkedList checkDuplicate(String hash);

    FileHash_Map getFilePath(String hash);

    int updatePath(String fileHash,String filePath);

    int addHashMap(String hash,String path,int userID);

    NetdiskFile getFileMapByHash(String hash);

    FileHash_Map getFileMapByPath(String path);

}
