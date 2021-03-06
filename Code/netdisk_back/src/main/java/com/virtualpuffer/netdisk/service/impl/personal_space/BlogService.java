package com.virtualpuffer.netdisk.service.impl.personal_space;

import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.entity.online_chat.Blog;
import com.virtualpuffer.netdisk.entity.online_chat.Comment;
import com.virtualpuffer.netdisk.enums.Accessible;
import com.virtualpuffer.netdisk.mapper.blog.SpaceBlogCommentMap;
import com.virtualpuffer.netdisk.mapper.blog.SpaceBlogMap;
import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;
import com.virtualpuffer.netdisk.utils.MybatisConnect;
import org.apache.ibatis.session.SqlSession;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 创建博客时，先建立属于作者的空博客（同时只能存在一个）
 * 这个博客没有任何内容，只有归属者（作者）和blog_id
 * 后续通过id获取对象并进行操作
 * */
public class BlogService extends BaseServiceImpl {
    public Blog blog;
    public boolean isHost;
    public AbstractPersonalSpace space;
    public Map<Integer,Blog> blog_Map;
    private static final Class threadLock = BlogService.class;
    protected CommentService commentService;
    private static Map<Integer, Integer> thumbMap = new HashMap();
    private static Map<Integer, Integer> executeMap = new HashMap();

    static {
        //5秒更新点赞
        Runnable runnable = new Runnable(){
            public void run() {
                SqlSession session = null;
                while (true) {
                    synchronized (threadLock) {
                        //交换Map,和点赞那边一起锁了
                        Map<Integer,Integer> temp = thumbMap;
                        thumbMap = executeMap;
                        executeMap = temp;
                    }
                    try {
                        Thread.sleep(5000);
                        if (!executeMap.isEmpty()) {
                            session = MybatisConnect.getSession();
                            for(Integer key : executeMap.keySet()){
                                try {
                                    SpaceBlogMap blogMap = session.getMapper(SpaceBlogMap.class);

                                    blogMap.addThumb(key,executeMap.get(key));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            session.commit();
                        }
                    } catch (Exception e){
                    }finally {
                        close(session);
                    }
                }
            }
        };
        new Thread(runnable).start();
    }

    public BlogService(int blog_id,boolean isHost,AbstractPersonalSpace space){
        SqlSession session = null;
        this.space = space;
        try{
            session = MybatisConnect.getSession();
            this.blog = session.getMapper(SpaceBlogMap.class).getBlog(blog_id);
            this.commentService = new CommentService(this.blog);
            this.isHost = isHost;
        }finally {
            close(session);
        }
    }

    public Map<Integer,Comment> getCommentMap(){
        return this.commentService.getCommentMap();
    }

    public CommentService getCommentService(int comment_id){
        return new CommentService(this.blog,comment_id,isHost);
    }

    public static int buildBlog(User user){
        Blog newBlog = null;
        SqlSession session = null;
        try {
            synchronized (threadLock) {
                session = MybatisConnect.getSession();
                newBlog = session.getMapper(SpaceBlogMap.class).getTempBlog(user.getUSER_ID());
                if(newBlog == null){
                    session.getMapper(SpaceBlogMap.class).makeBlog(null,user.getUSER_ID(),null);
                    newBlog = session.getMapper(SpaceBlogMap.class).getTempBlog(user.getUSER_ID());
                }
                session.commit();
            }
        }finally {
            close(session);
        }
        return newBlog.getBlog_id();
    }

    public void buildBlog(String contentText, Accessible access){
        if(!isHost){
            throw new RuntimeException("没有操作权限");
        }else {
            if(blog.getBlog_tag() == 0){
                SqlSession session = null;
                Timestamp time = new Timestamp(System.currentTimeMillis());
                try {
                    session = MybatisConnect.getSession();
                    session.getMapper(SpaceBlogMap.class).buildBlog(contentText,time,blog.getBlog_id(),access.name());
                    session.commit();
                }finally {
                    close(session);
                }
            }else {
                throw new RuntimeException("该博客已经发表");
            }
        }
    }

    public void addComment(Comment comment){
        if (!isHost) {
            throw new RuntimeException("没有操作权限");
        } else {
            SqlSession session = null;
            comment.setTimestamp(getTimestamp());
            try {
                session = MybatisConnect.getSession();
                session.getMapper(SpaceBlogCommentMap.class).makeComment(comment);
                session.commit();
            } finally {
                close(session);
            }
        }
    }

    public void addThumb(){
        Integer thumbNumber = 0;
        int id = blog.getUser_id();
        synchronized (threadLock) {
            if ((thumbNumber = thumbMap.get(id))!= null) {
                thumbMap.put(id,thumbNumber);
            }else {
                thumbMap.put(id,1);
            }
        }
    }
    public void delete(){
        if(!isHost){
            throw new RuntimeException("没有操作权限");
        }else {
            SqlSession session =  null;
            try {
                session = MybatisConnect.getSession();
                session.getMapper(SpaceBlogMap.class).deleteBlog(this.blog.getBlog_id());
                session.commit();
            } finally {
                close(session);
            }
        }
    }
}
