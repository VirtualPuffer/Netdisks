package com.virtualpuffer.netdisk.entity.online_chat;

import com.virtualpuffer.netdisk.Security.securityFilter.BaseFilter;
import com.virtualpuffer.netdisk.entity.BaseEntity;
import com.virtualpuffer.netdisk.entity.User;

import java.sql.Timestamp;

public class Comment extends BaseEntity {
    private String name;
    private String USER_ID;
    private int thumb;//点赞
    private Timestamp time;
    private int blog_id;
    private int comment_id;
    public int replyComment_id;
    public String contentText;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public int getThumb() {
        return thumb;
    }

    public void setThumb(int thumb) {
        this.thumb = thumb;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
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

    public int getReplyComment_id() {
        return replyComment_id;
    }

    public void setReplyComment_id(int replyComment_id) {
        this.replyComment_id = replyComment_id;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }
}
