package com.virtualpuffer.netdisk.entity.online_chat;

import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.enums.Accessible;

import java.util.LinkedList;

/**
 * 父类
 *     public int user_id;
 *     public int thumb;//点赞
 *     public String contentText;
 *
 * */
public class Blog extends AbstractBlog {
    private int blog_id;
    public String time;
    public int blog_tag;//0表示临时，1表示完成
    public Accessible accessible;
    public LinkedList<String> photoList;
    public LinkedList<Comment> commentList;

    public Blog() {
    }

    public int getBlog_tag() {
        return blog_tag;
    }

    public void setBlog_tag(int blog_tag) {
        this.blog_tag = blog_tag;
    }

    public void setAccessible(Accessible accessible) {
        this.accessible = accessible;
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

    public Accessible getAccessible() {
        return accessible;
    }

    public LinkedList<String> getPhotoList() {
        return photoList;
    }

    public LinkedList<Comment> getCommentList() {
        return commentList;
    }
}
