package com.virtualpuffer.netdisk.entity;

public class FileHash_Map {
    String File_Hash;
    int USER_ID;
    String path;

    public FileHash_Map() {
    }

    public FileHash_Map(String File_Hash, int USER_ID, String path) {
        this.File_Hash = File_Hash;
        this.USER_ID = USER_ID;
        this.path = path;
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
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
