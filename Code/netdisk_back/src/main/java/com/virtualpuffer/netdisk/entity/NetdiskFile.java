package com.virtualpuffer.netdisk.entity;

import com.virtualpuffer.netdisk.utils.StringUtils;

import static com.virtualpuffer.netdisk.utils.StringUtils.filePathDeal;

public class NetdiskFile {
    private String File_Name;
    private String File_Path;
    private String File_Destination;
    private String File_Hash;
    private int userID;//拥有者ID
    private boolean lock = false;

    public NetdiskFile handleInstance()throws RuntimeException{
        if(this.lock == false){
            this.File_Destination = filePathDeal(this.File_Destination);
            this.lock = true;
            return this;
        }else {
            throw new RuntimeException("NetdiskFile has been handle");
        }
    }

    public NetdiskFile handleInstance(String source)throws RuntimeException{
        if(this.lock == false){
            int length = filePathDeal(source).length();
            this.File_Destination = filePathDeal(this.File_Destination.substring(length));
            this.lock = true;
            return this;
        }else {
            throw new RuntimeException("NetdiskFile has been handle");
        }
    }


    public NetdiskFile(){}

    public String getFile_Name() {
        return File_Name;
    }

    public void setFile_Name(String file_Name) {
        File_Name = file_Name;
    }

    public String getFile_Path() {
        return File_Path;
    }

    public void setFile_Path(String file_Path) {
        File_Path = file_Path;
    }

    public String getFile_Destination() {
        return File_Destination;
    }

    public void setFile_Destination(String file_Destination) {
        File_Destination = file_Destination;
    }

    public String getFile_Hash() {
        return File_Hash;
    }

    public void setFile_Hash(String file_Hash) {
        File_Hash = file_Hash;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
