package com.virtualpuffer.netdisk.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;


@Singleton
public class MybatisConnect {
    private static SqlSessionFactory factory;
    private static volatile MybatisConnect mybatisFactory;
    private LinkedList<SqlSession> SessionList;

    private MybatisConnect(){}
    //启动链接
    static {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            factory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MybatisConnect getMybatisConnect(){
        if(mybatisFactory == null){//1
            synchronized (MybatisConnect.class){//2
                if(mybatisFactory == null){//3
                    mybatisFactory = new MybatisConnect();//4
                }
            }
        }
        return mybatisFactory;
    }

    public void setSessionList(LinkedList<SqlSession> sessionList) {
        SessionList = sessionList;
    }

    public LinkedList getSessionList(){
        return SessionList;
    }
    public static SqlSession getSession(){
        SqlSession session = factory.openSession();
      /*  getMybatisConnect().getSessionList().add(session);*/
        return session;
    }

}
