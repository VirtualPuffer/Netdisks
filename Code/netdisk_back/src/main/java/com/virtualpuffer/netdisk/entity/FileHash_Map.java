package com.virtualpuffer.netdisk.entity;

public class FileHash_Map {
    String Hash;
    int USER_ID;
    String path;

    public FileHash_Map() {
    }

    public FileHash_Map(String hash, int USER_ID, String path) {
        Hash = hash;
        this.USER_ID = USER_ID;
        this.path = path;
    }

    public String getHash() {
        return Hash;
    }

    public void setHash(String hash) {
        Hash = hash;
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
