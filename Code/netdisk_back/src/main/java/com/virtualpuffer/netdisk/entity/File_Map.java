package com.virtualpuffer.netdisk.entity;

public class File_Map {
    String USER_ID;
    String File_Name;
    String File_Path;
    String File_Hash;

    public File_Map() {
    }

    public File_Map(String USER_ID, String file_Name, String file_Path, String file_Hash) {
        this.USER_ID = USER_ID;
        File_Name = file_Name;
        File_Path = file_Path;
        File_Hash = file_Hash;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public String getFile_Name() {
        return File_Name;
    }

    public String getFile_Path() {
        return File_Path;
    }

    public String getFile_Hash() {
        return File_Hash;
    }
}
