package com.virtualpuffer.netdisk.service.impl;

import com.virtualpuffer.netdisk.MybatisConnect;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.LoginHistory;
import com.virtualpuffer.netdisk.mapper.UserMap;
import com.virtualpuffer.netdisk.service.LoginService;
import com.virtualpuffer.netdisk.utils.TestTime;
import org.apache.catalina.Session;
import org.apache.ibatis.session.SqlSession;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.management.RuntimeErrorException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class UserServiceImpl extends BaseServiceImpl implements LoginService {
    /**
    * 初始阶段只传入账号密码，登录状态
     * 解析token
     * 登录成功后完善内容
    *
    * */
    private User  user;
    private static final long time = 7*24*60*60;
    public static final String DefaultWare = getMess("defaultWare");

    public UserServiceImpl(User loginUser){
        this.user = loginUser;
        Map<String,Object> map = new HashMap();
        map.put("username",user.getUsername());
        map.put("password",user.getPassword());
        map.put("userID",user.getUSER_ID());
        map.put("ip",user.getIp());
        String token = createToken(60*60*24,map,user.getUsername(),null);
        user.setToken(token);
    }


    /**
     * @param token token参数
     * 解析token，获取用户名和密码并进行匹配
     * 匹配成功后会返回服务对象
    * */
    public static UserServiceImpl getInstance(String token, String ip) throws RuntimeException{
        Map map = parseJWT(token);
  /*      if(map.get("ip").equals(ip)){*/
            return getInstance((String)map.get("username"),(String)map.get("password"),false , null);
      /*  }
        throw new RuntimeException("ip验证失败");*/
    }
    //登录这里过来
    public static UserServiceImpl getInstance(User user, @Nullable HttpServletRequest request)throws RuntimeException{
        return getInstance(user.getUsername(),user.getPassword(),!(user.getIp() == null || user.getIp().equals("")) ? true : false , user.getIp());
    }
    /**
     * @param username 用户名
     * @param password 密码
     * @param persistence 是否记录登录
    * 顶级方法，生成登录对象
    * */
    public static UserServiceImpl getInstance(String username, String password, @NonNull boolean persistence , String ip) throws RuntimeException{
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            User user = session.getMapper(UserMap.class).userLogin(username,password);
            ;
            if(user == null || password == null || username == null){
                throw new RuntimeException("用户名或者密码错误");
            }else if(persistence && !(ip == null || ip.equals(""))){
                int x = session.getMapper(LoginHistory.class).loginPersistence(user.getUSER_ID(),ip,new Timestamp(System.currentTimeMillis()),0,System.currentTimeMillis());
            }
            session.commit();
            return new UserServiceImpl(user);
        } finally {
            close(session);
        }
    }

    public static void registerUser(User user){
        registerUser(user.getUsername(),user.getPassword(),user.getName());
    }

    public static void registerUser(String username,String password,String name)throws RuntimeException,Error{
        if(username == null || password == null || name == null || username.equals("") || password.equals("") || name.equals("")){
            StringBuffer buffer = new StringBuffer("缺少参数:");
            buffer.append(username == null || username.equals("") ? "username " : "");
            buffer.append(password == null || password.equals("")? "password " : "");
            buffer.append(name == null || name.equals("") ? "name " : "");
            throw new RuntimeException(buffer.toString());
        }
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            UserMap map = session.getMapper(UserMap.class);
            User user = map.duplicationUsername(username);
            if(user != null){
                throw new RuntimeException("用户名已经存在");
            }else {
                int count = map.register(username,password,name);
                map.updateURL();
                if(count!=1){
                    throw new Error("注册失败");
                }
            }
            int userID = map.getIDbyName(username);
            if(registerBuild(userID)){//创建仓库
                session.commit();
            }else {
                throw new Error("boom");
            }
            ;
        } finally {
            close(session);
        }
    }

    public static boolean registerBuild(int userID){
        String path = DefaultWare + userID;
        File on = new File(path);
        try {
            System.out.println(on.getCanonicalPath());
        } catch (IOException e) {
            System.out.println(on.getAbsolutePath());
        }
        if(on.exists() || on.mkdir()){
            return true;
        }
        return false;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
