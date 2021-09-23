package com.virtualpuffer.netdisk.service.impl.personal_space;

import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.entity.online_chat.Blog;
import com.virtualpuffer.netdisk.entity.online_chat.Comment;
import com.virtualpuffer.netdisk.mapper.blog.SpaceBlogCommentMap;
import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;
import com.virtualpuffer.netdisk.utils.MybatisConnect;
import org.apache.ibatis.session.SqlSession;

import java.sql.Timestamp;
import java.util.Map;


public class CommentService extends BaseServiceImpl {
    public Map<Integer,Comment> commentMap;

    public CommentService(Blog blog){
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            this.commentMap = session.getMapper(SpaceBlogCommentMap.class).getComment(blog.getBlog_id());
        } finally {
            close(session);
        }
    }
    public static void buildComment(Blog blog,Comment comment){
        SqlSession session = null;
        comment.setTime(getTimestamp());
        try {
            session = MybatisConnect.getSession();
            session.getMapper(SpaceBlogCommentMap.class).makeComment(comment);
            session.commit();
        } finally {
            close(session);
        }
    }

    public Map<Integer, Comment> getCommentMap() {
        return commentMap;
    }

    public void thumb(int user_id){}

    public void deleteComment(){}

    public void buildComment(String contentText){

    }
}
