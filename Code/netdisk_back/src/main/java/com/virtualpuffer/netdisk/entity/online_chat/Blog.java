package com.virtualpuffer.netdisk.entity.online_chat;

import com.virtualpuffer.netdisk.entity.BaseEntity;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.enums.Accessible;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;

/**
 * 父类
 *     public int user_id;
 *     public int thumb;//点赞
 *     public String contentText;
 *
 * */
public class Blog extends BaseEntity implements Serializable {
    public int user_id;
    public int thumb;//点赞
    public String contentText;
    private int blog_id;
    public String time;
    public int blog_tag;//0表示临时，1表示完成
    public Accessible access;
    public LinkedList<String> photoList;
    public Map<Integer,Comment> commentMap;

    public Blog() {
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setThumb(int thumb) {
        this.thumb = thumb;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public void setBlog_id(int blog_id) {
        this.blog_id = blog_id;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public int getBlog_tag() {
        return blog_tag;
    }

    public void setBlog_tag(int blog_tag) {
        this.blog_tag = blog_tag;
    }

    public void setAccess(Accessible accessible) {
        this.access = accessible;
    }

    public int getBlog_id() {
        return blog_id;
    }

    public String getTime() {
        return time;
    }

    public void setPhotoList(LinkedList<String> photoList) {
        this.photoList = photoList;
    }

    public int getID() {
        return user_id;
    }

    public int getThumb() {
        return thumb;
    }

    public String getContentText() {
        return contentText;
    }

    public Accessible getAccess() {
        return access;
    }

    public LinkedList<String> getPhotoList() {
        return photoList;
    }

    public Map<Integer, Comment> getCommentMap() {
        return commentMap;
    }

    public void setCommentMap(Map<Integer, Comment> commentList) {
        this.commentMap = commentList;
    }
}
