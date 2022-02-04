package com.virtualpuffer.netdisk.enums;

public enum ContentType {
    HTML("text/html"),FILE("application/octet-stream");
    public String content_type;
    ContentType(String content_Type){
        this.content_type = content_Type;
    }
}
