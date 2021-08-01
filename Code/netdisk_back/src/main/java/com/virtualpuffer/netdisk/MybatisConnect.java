package com.virtualpuffer.netdisk;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;


@Singleton
public class MybatisConnect {
    private static SqlSessionFactory factory;
    private static final MybatisConnect mybatisFactory = new MybatisConnect();


    //启动链接
    static {
        try {
            String resource = "mapper.mysql.mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            factory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static MybatisConnect getMybatisConnect(){
        return mybatisFactory;
    }
    private MybatisConnect(){}


    public static SqlSession getSession(){
        System.out.println("分发对象");
        return factory.openSession();
    }

}
