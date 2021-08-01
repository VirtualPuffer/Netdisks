package com.virtualpuffer.netdisk.entity;

public class Login_Verify {
    String ip;
    String date;
    String ver;
    String clock;
    int USER_ID;
    public Login_Verify(){}

    public Login_Verify(String ip, String date, String ver, String clock, int USER_ID) {
        this.ip = ip;
        this.date = date;
        this.ver = ver;
        this.clock = clock;
        this.USER_ID = USER_ID;
    }

    public String getIp() {
        return ip;
    }

    public String getDate() {
        return date;
    }

    public String getVer() {
        return ver;
    }

    public String getClock() {
        return clock;
    }

    public int getUSER_ID() {
        return USER_ID;
    }
}
