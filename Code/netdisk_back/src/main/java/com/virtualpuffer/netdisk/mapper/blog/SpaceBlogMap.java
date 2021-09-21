package com.virtualpuffer.netdisk.mapper.blog;

import com.virtualpuffer.netdisk.entity.online_chat.Blog;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Mapper
public interface SpaceBlogMap {
    Blog getBlog(int blog_id);

    @MapKey("blog_id")
    Map<Integer,Blog> getAllBlog(int USER_ID);

    @MapKey("blog_id")
    Map<Integer,Blog> getAllPublicBlog(int USER_ID);

    Blog getTempBlog(int USER_ID);

    int deleteBlog(int blog_id);
    int addThumb(int comment_id,int number);
    int buildBlog(String contentText, Timestamp time,int blog_id);
    int makeBlog(String contentText,int USER_ID,Timestamp time);

}
