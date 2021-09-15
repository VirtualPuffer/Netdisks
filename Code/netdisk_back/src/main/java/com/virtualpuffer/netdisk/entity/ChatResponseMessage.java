package com.virtualpuffer.netdisk.entity;

import com.virtualpuffer.netdisk.utils.Log;

public class ChatResponseMessage {
    private String time;
    private String user;
    private String content;
    private String target;

    public ChatResponseMessage() {
    }

    public ChatResponseMessage(String user, String content) {
        this.user = user;
        this.content = content;
        this.time = Log.getTime();
    }

    public ChatResponseMessage(String time, String user, String content) {
        this.time = time;
        this.user = user;
        this.content = content;
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
