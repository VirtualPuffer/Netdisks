package com.virtualpuffer.netdisk.service.impl.personal_space;

import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.entity.online_chat.Blog;
import com.virtualpuffer.netdisk.mapper.blog.SpaceBlogMap;
import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;
import com.virtualpuffer.netdisk.utils.MybatisConnect;
import org.apache.ibatis.session.SqlSession;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建博客时，先建立属于作者的空博客（同时只能存在一个）
 * 这个博客没有任何内容，只有归属者（作者）和blog_id
 * 后续通过id获取对象并进行操作
 * */
public class BlogService extends BaseServiceImpl {
    public Blog blog;
    public boolean isHost;
    private static final Class threadLock = BlogService.class;
    private static Map<Integer, Integer> thumbMap = new HashMap();
    private static Map<Integer, Integer> executeMap = new HashMap();

    static {
        //5秒更新点赞
        Runnable runnable = new Runnable(){
            public void run() {
                SqlSession session = null;
                while (true) {
                    synchronized (threadLock) {
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
                                    session.getMapper(SpaceBlogMap.class).addThumb(key,executeMap.get(key));
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

    public BlogService(int blog_id,User user){
        SqlSession session = null;
        try{
            session = MybatisConnect.getSession();
            this.blog = session.getMapper(SpaceBlogMap.class).getBlog(blog_id);
            if(blog.getUser_id() == user.getUSER_ID()){
                isHost = true;
            }else {
                isHost = false;
            }
        }finally {
            close(session);
        }
    }

    public BlogService(Blog blog){
            this.blog = blog;
            isHost = true;
    }

    public static Blog buildBlog(User user){
        Blog newBlog = null;
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            newBlog = session.getMapper(SpaceBlogMap.class).getTempBlog(user.getUSER_ID());
            if(newBlog == null){
                session.getMapper(SpaceBlogMap.class).makeBlog(null,user.getUSER_ID(),null);
                newBlog = session.getMapper(SpaceBlogMap.class).getTempBlog(user.getUSER_ID());
            }
            session.commit();
        }finally {
            close(session);
        }
        return newBlog;
    }

    public void buildBlog(String contentText){
        if(blog.getBlog_tag() == 0){
            SqlSession session = null;
            Timestamp time = new Timestamp(System.currentTimeMillis());
            try {
                session = MybatisConnect.getSession();
                session.getMapper(SpaceBlogMap.class).buildBlog(contentText,time,blog.getBlog_id());
                session.commit();
            }finally {
                close(session);
            }
        }else {
            throw new RuntimeException("该博客已经发表");
        }
    }
    public void addThumb(){
        Integer thumbNumber = 0;
        int id = blog.getID();
        synchronized (threadLock) {
            if ((thumbNumber = thumbMap.get(id))!= null) {
                thumbMap.put(id,thumbNumber);
            }else {
                thumbMap.put(id,1);
            }
        }
    }
    public void delete(){
        SqlSession session =  null;
        try {
            session = MybatisConnect.getSession();
            session.getMapper(SpaceBlogMap.class).deleteBlog(this.blog.getBlog_id());
        } finally {
            close(session);
        }
    }
}
