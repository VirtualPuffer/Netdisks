package com.virtualpuffer.netdisk.mapper.blog;

import com.virtualpuffer.netdisk.entity.online_chat.Blog;

import java.sql.Timestamp;
import java.util.LinkedList;

public interface SpaceBlogMap {
    LinkedList<Blog> all();
    LinkedList<Blog> getAllBlog(int USER_ID);
    int deleteBlog(int blog_id);
    int addThumb(int blog_id);
    int makeBlog(String contentText,int USER_ID,Timestamp time);
    int makeComment(String contentText, int reply_comment_id,int blog_id,int USER_ID,Timestamp time);
}
