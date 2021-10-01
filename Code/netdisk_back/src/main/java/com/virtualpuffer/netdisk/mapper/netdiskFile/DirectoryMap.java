package com.virtualpuffer.netdisk.mapper.netdiskFile;

import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskDirectory;

import java.util.LinkedList;

public interface DirectoryMap {
    AbsoluteNetdiskDirectory getDirectory(int USER_ID,String destination);

    AbsoluteNetdiskDirectory getChildrenDirectory(int USER_ID,int Directory_Parent_ID,String childrenDirectoryName);

    LinkedList<AbsoluteNetdiskDirectory> getDir(int USER_ID, int Directory_ID);

    LinkedList<AbsoluteNetdiskDirectory> onExists(int USER_ID,int Directory_Parent_ID,String Directory_Name);

    int mkdir(int USER_ID,String name,int Directory_Parent_ID);
}
