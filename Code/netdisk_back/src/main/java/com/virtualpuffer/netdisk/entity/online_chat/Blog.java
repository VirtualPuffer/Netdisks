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
    public Accessible accessible;
    public LinkedList<String> photoList;
    public LinkedList<Comment> commentList;

    public Blog() {
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
