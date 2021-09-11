package com.virtualpuffer.netdisk.entity;

public class User_WareHouse {
    int USER_ID;
    int Ware_Capacity;
    int Current_File_Size;
    String Ware_Location;
    int Ware_id;

    public User_WareHouse() {
    }

    public int getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(int USER_ID) {
        this.USER_ID = USER_ID;
    }

    public int getWare_Capacity() {
        return Ware_Capacity;
    }

    public void setWare_Capacity(int ware_Capacity) {
        Ware_Capacity = ware_Capacity;
    }

    public int getCurrent_File_Size() {
        return Current_File_Size;
    }

    public void setCurrent_File_Size(int current_File_Size) {
        Current_File_Size = current_File_Size;
    }

    public String getWare_Location() {
        return Ware_Location;
    }

    public void setWare_Location(String ware_Location) {
        Ware_Location = ware_Location;
    }

    public int getWare_id() {
        return Ware_id;
    }

    public void setWare_id(int ware_id) {
        Ware_id = ware_id;
    }
}
