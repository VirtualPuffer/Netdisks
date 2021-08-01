package com.virtualpuffer.netdisk.enums;

public enum ErrorCode {
    DEFAULT(9001),
    DBCONNECT(9002);

    private int code;

    private ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
