package com.virtualpuffer.netdisk.entity.file;

public class FileHash_Map {
    String File_Hash;
    int USER_ID;
    String File_Path;

    public FileHash_Map() {
    }

    public FileHash_Map(String File_Hash, int USER_ID, String path) {
        this.File_Hash = File_Hash;
        this.USER_ID = USER_ID;
        this.File_Path = path;
    }

    public String getFile_Hash() {
        return File_Hash;
    }

    public void setFile_Hash(String hash) {
        File_Hash = hash;
    }

    public int getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(int USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getPath() {
        return File_Path;
    }

    public void setPath(String path) {
        this.File_Path = path;
    }
}
