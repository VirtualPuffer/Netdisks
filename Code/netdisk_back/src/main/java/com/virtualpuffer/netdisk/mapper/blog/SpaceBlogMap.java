package com.virtualpuffer.netdisk.mapper.blog;

import com.virtualpuffer.netdisk.entity.online_chat.Blog;

import java.sql.Timestamp;
import java.util.LinkedList;

public interface SpaceBlogMap {
    LinkedList<Blog> getAllBlog(int USER_ID);
    int deleteBlog(int blog_id);
    int addThumb(int blog_id);
    int makeBlog(String contentText,int USER_ID,Timestamp time);
}
