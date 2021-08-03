package com.virtualpuffer.netdisk.mapper;

import java.util.LinkedList;

public interface FileHashMap {
    /**
     * 上传文件
     * */
    LinkedList checkDuplicate(String hash);

    int addHashMap(String hash,String path,int userID);

}
