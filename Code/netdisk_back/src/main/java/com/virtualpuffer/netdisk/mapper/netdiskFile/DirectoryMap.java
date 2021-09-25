package com.virtualpuffer.netdisk.mapper.netdiskFile;

import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskDirectory;

public interface DirectoryMap {
    AbsoluteNetdiskDirectory getDirectory(int USER_ID,String destination);

    int onExists(int USER_ID,String destination);

    int mkdir(int USER_ID,String name,int Directory_Parent_ID);
}
