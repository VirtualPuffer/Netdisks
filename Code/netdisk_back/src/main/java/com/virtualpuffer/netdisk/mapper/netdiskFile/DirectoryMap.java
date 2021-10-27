package com.virtualpuffer.netdisk.mapper.netdiskFile;

import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskDirectory;
import org.apache.ibatis.annotations.MapKey;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

public interface DirectoryMap {
    int delete(int Directory_ID,int USER_ID);

    AbsoluteNetdiskDirectory getDirectoryByID(int Directory_ID);

    AbsoluteNetdiskDirectory getDirectory(int USER_ID,String destination);

    AbsoluteNetdiskDirectory getChildrenDirectory(int USER_ID,int Directory_Parent_ID,String childrenDirectoryName);

    LinkedList<String> getDir(int USER_ID, int Directory_ID);

    AbsoluteNetdiskDirectory onExists(int USER_ID,int Directory_Parent_ID,String Directory_Name);

    int mkdir(int USER_ID,String name,int Directory_Parent_ID);

    int rename(int USER_ID,int Directory_ID,String new_Directory_Name);

    HashSet<AbsoluteNetdiskDirectory> getChildrenDirID(int Dirctory_ID);

    LinkedList<AbsoluteNetdiskDirectory> searchDir(String name,int USER_ID);

    @MapKey("Directory_ID")
    HashMap<Integer,AbsoluteNetdiskDirectory> getDirMap(int USER_ID);
}
