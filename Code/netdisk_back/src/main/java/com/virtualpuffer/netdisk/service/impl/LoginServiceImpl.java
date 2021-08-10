package com.virtualpuffer.netdisk.service.impl;

import com.virtualpuffer.netdisk.MybatisConnect;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.LoginHistory;
import com.virtualpuffer.netdisk.mapper.UserMap;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.ibatis.session.SqlSession;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Null;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginServiceImpl extends BaseServiceImpl {
    /**
    * 初始阶段只传入账号密码，登录状态
     * 解析token
     * 登录成功后完善内容
    *
    * */
    private User  user;
    private static final long time = 7*24*60*60;

    public LoginServiceImpl(User loginUser){
        this.user = loginUser;
        Map<String,Object> map = new HashMap();
        map.put("username",user.getUsername());
        map.put("password",user.getPassword());
        map.put("userID",user.getUSER_ID());
        map.put("ip",user.getIp());
        String token = createToken(1000,map,user.getUsername());
        user.setToken(token);
    }


    /**
     * @param token token参数
     * 解析token，获取用户名和密码并进行匹配
     * 匹配成功后会返回服务对象
    * */
    public static LoginServiceImpl getInstance(String token,String ip) throws RuntimeException{
        Map map = parseJWT(token);
        if(map.get("ip") == ip){
            return getInstance((String)map.get("username"),(String)map.get("password"),false , null);
        }
        throw new RuntimeException();
    }
    //登录这里过来
    public static LoginServiceImpl getInstance(User user,@Nullable HttpServletRequest request)throws RuntimeException{
        return getInstance(user.getUsername(),user.getPassword(),!(user.getIp() == null || user.getIp().equals("")) ? true : false , user.getIp());
    }
    /**
     * @param username 用户名
     * @param password 密码
     * @param persistence 是否记录登录
    * 顶级方法，生成登录对象
    * */
    public static LoginServiceImpl getInstance(String username, String password, @NonNull boolean persistence , String ip) throws RuntimeException{
        System.out.println(persistence + "///////////////////////////////////////////////////");
        SqlSession session = MybatisConnect.getSession();
        User user = session.getMapper(UserMap.class).getUserByUsername(username,password);
        if(user == null ){
            throw new RuntimeException("用户名或者密码错误");
        }else if(persistence && !(ip == null || ip.equals(""))){
            int x = session.getMapper(LoginHistory.class).loginPersistence(user.getUSER_ID(),ip,new Timestamp(System.currentTimeMillis()),0,System.currentTimeMillis());
        }
        session.commit();
        close(session);
        return new LoginServiceImpl(user);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
