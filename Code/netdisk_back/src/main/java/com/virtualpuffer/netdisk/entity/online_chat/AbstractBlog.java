package com.virtualpuffer.netdisk.entity.online_chat;

import com.virtualpuffer.netdisk.entity.BaseEntity;
import com.virtualpuffer.netdisk.entity.User;

public abstract class AbstractBlog extends BaseEntity {
    public int user_id;
    public int thumb;//点赞
    public String contentText;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getThumb() {
        return thumb;
    }

    public void setThumb(int thumb) {
        this.thumb = thumb;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }
}
