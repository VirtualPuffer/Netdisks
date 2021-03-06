package com.virtualpuffer.netdisk.entity.file;

import org.springframework.web.multipart.MultipartFile;

public class File_Map {
    String USER_ID;
    String File_Name;
    String File_Destination;
    String File_Hash;
    String path;
    String destination;
    MultipartFile getFile;

    public File_Map() {
    }

    public File_Map(String USER_ID, String file_Name, String file_Destination, String file_Hash) {
        this.USER_ID = USER_ID;
        this.File_Name = file_Name;
        this.File_Destination = file_Destination;
        this.File_Hash = file_Hash;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public String getFile_Name() {
        return File_Name;
    }

    public String getFile_Destination() {
        return File_Destination;
    }

    public String getFile_Hash() {
        return File_Hash;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public MultipartFile getGetFile() {
        return getFile;
    }

    public void setGetFile(MultipartFile getFile) {
        this.getFile = getFile;
    }

    @Override
    public String toString() {
        return "File_Map{" +
                "USER_ID='" + USER_ID + '\'' +
                ", File_Name='" + File_Name + '\'' +
                ", File_Destination='" + File_Destination + '\'' +
                ", File_Hash='" + File_Hash + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
