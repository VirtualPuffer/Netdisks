package com.virtualpuffer.netdisk.mapper;

import java.util.LinkedList;

public interface FileHashMap {
    /**
     * 上传文件
     * */
    LinkedList checkDuplicate(String hash);

    LinkedList getFilePath(String hash);

    int updatePath(String fileHash,String filePath);

    int addHashMap(String hash,String path,int userID);

}
