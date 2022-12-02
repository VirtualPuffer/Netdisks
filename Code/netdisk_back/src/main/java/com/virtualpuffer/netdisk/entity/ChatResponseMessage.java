package com.virtualpuffer.netdisk.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.virtualpuffer.netdisk.utils.Log;

public class ChatResponseMessage {
    private String time;
    private String user;
    private String content;
    private String target;
    private String type;
    private int message_id;

    @JSONField(name = "uid")
    private int USER_ID;

    public ChatResponseMessage() {
    }

    public ChatResponseMessage(String user, String content) {
        this.user = user;
        this.content = content;
        this.time = Log.getTime();
    }

    public ChatResponseMessage(String time, String user, String content,int message_id) {
        this.time = time;
        this.user = user;
        this.content = content;
        this.message_id = message_id;
    }

    public ChatResponseMessage(String time, String user, String content,int message_id,int USER_ID,String type) {
        this.time = time;
        this.user = user;
        this.content = content;
        this.message_id = message_id;
        this.USER_ID = USER_ID;
        this.type = type;
    }

    public String getType() {
        return type;
    }



    public void setType(String type) {
        this.type = type;
    }

    public int getMessage_id() {
        return message_id;
    }

    public int getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(int USER_ID) {
        this.USER_ID = USER_ID;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
