package com.virtualpuffer.netdisk.entity.online_chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.virtualpuffer.netdisk.Security.securityFilter.BaseFilter;
import com.virtualpuffer.netdisk.entity.BaseEntity;
import com.virtualpuffer.netdisk.entity.User;

import java.sql.Timestamp;

public class Comment extends BaseEntity {
    private String name;
    private int USER_ID;
    private int thumb;//点赞
    private String time;
    private Timestamp timestamp;
    private int blog_id;
    private int comment_id;
    public int reply_Comment_id;
    public String contentText;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(int USER_ID) {
        this.USER_ID = USER_ID;
    }

    public int getThumb() {
        return thumb;
    }

    public void setThumb(int thumb) {
        this.thumb = thumb;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @JsonIgnore
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(int blog_id) {
        this.blog_id = blog_id;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public int getReply_comment_id() {
        return reply_Comment_id;
    }

    public void setReply_comment_id(int replyComment_id) {
        this.reply_Comment_id = replyComment_id;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }
}
