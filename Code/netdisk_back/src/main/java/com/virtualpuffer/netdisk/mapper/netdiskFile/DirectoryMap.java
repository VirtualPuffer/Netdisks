package com.virtualpuffer.netdisk.mapper.netdiskFile;

import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskDirectory;

import java.util.HashSet;
import java.util.LinkedList;

public interface DirectoryMap {
    AbsoluteNetdiskDirectory getDirectoryByID(int Directory_ID);

    AbsoluteNetdiskDirectory getDirectory(int USER_ID,String destination);

    AbsoluteNetdiskDirectory getChildrenDirectory(int USER_ID,int Directory_Parent_ID,String childrenDirectoryName);

    LinkedList<String> getDir(int USER_ID, int Directory_ID);

    LinkedList<AbsoluteNetdiskDirectory> onExists(int USER_ID,int Directory_Parent_ID,String Directory_Name);

    int mkdir(int USER_ID,String name,int Directory_Parent_ID);

    int rename(int USER_ID,int Directory_ID,String new_Directory_Name);

    HashSet<AbsoluteNetdiskDirectory> getChildrenDirID(int Dirctory_ID);
}
