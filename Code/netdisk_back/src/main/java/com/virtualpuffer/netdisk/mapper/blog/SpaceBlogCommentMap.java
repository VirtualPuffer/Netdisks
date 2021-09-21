package com.virtualpuffer.netdisk.mapper.blog;

import com.virtualpuffer.netdisk.entity.online_chat.Comment;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Map;

@Mapper
public interface SpaceBlogCommentMap {
    int addThumb(int comment_id,int number);
    int deleteComment(int comment_id);
    int makeComment(String contentText, int reply_comment_id, int blog_id, int USER_ID, Timestamp time);

    @MapKey("comment_id")
    Map<Integer,Comment> getComment(int blog_id);
}
