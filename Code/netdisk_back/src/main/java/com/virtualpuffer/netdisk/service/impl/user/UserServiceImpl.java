package com.virtualpuffer.netdisk.service.impl.user;

import com.virtualpuffer.netdisk.MybatisConnect;
import com.virtualpuffer.netdisk.data.Mail;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.LoginHistory;
import com.virtualpuffer.netdisk.mapper.UserMap;
import com.virtualpuffer.netdisk.service.LoginService;
import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;
import com.virtualpuffer.netdisk.service.messageService.PortMessage;
import com.virtualpuffer.netdisk.service.messageService.SendMail;
import org.apache.ibatis.session.SqlSession;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class UserServiceImpl extends BaseServiceImpl implements LoginService {
    /**
    * 初始阶段只传入账号密码，登录状态
     * 解析token
     * 登录成功后完善内容
    *
     * tokenTag用于标识token用处
    * */
    protected User  user;
    protected String tokenTag;
    public static final long Time = 7*24*60*60;
    public static final String resetURL = getMess("resetURL");
    public static final String DefaultWare = getMess("defaultWare");

    //redis状态标识
    public static final String TOKEN_ACTIVE = "active";
    public static final String TOKEN_EXPIRE = "expire";
    //token作用标识
    public static final String LOGIN_TAG = "login";
    public static final String RESET_TAG = "reset";

    public UserServiceImpl(User loginUser){
        this.user = loginUser;
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

    public static UserServiceImpl getInstanceByAddr(String mailAddr) throws RuntimeException{
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            User user = session.getMapper(UserMap.class).getInstanceByAddr(mailAddr);
            if(user == null){
                throw new RuntimeException("邮箱地址错误");
            }
            return new UserServiceImpl(user);
        } finally {
            close(session);
        }
    }

    public static void registerUser(User user){
        registerUser(user.getUsername(),user.getPassword(),user.getName(),user.getAddress());
    }

    public static void registerUser(String username,String password,String name,String address)throws RuntimeException,Error{
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
            User addrUser = map.getInstanceByAddr(address);
            User user = map.duplicationUsername(username);
            if(user != null){
                throw new RuntimeException("用户名已经存在");
            }else if(addrUser != null){
                throw new RuntimeException("邮箱地址已经被注册");
            }else {
                int count = map.register(username,password,name,address);
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
/*        try {
            System.out.println(on.getCanonicalPath());
        } catch (IOException e) {
            System.out.println(on.getAbsolutePath());
        }*/
        if(on.exists() || on.mkdir()){
            return true;
        }
        return false;
    }
    public void sendResetMail(){
        sendMess(resetURL());
    }

    private String resetURL(){
        Map<String,Object> map = new HashMap();
        map.put("tokenTag",RESET_TAG);
        map.put("username",user.getUsername());
        map.put("password",user.getPassword());
        map.put("userID",user.getUSER_ID());
        map.put("ip",user.getIp());
        String token = createToken(60*10,map,user.getUsername(),null);
        return resetURL + token;
    }

    public void sendMess(String msg){

        SendMail.sendEmail(Mail.buildMail(this.user.getAddress(),"网盘密码找回邮件",msg));
    }

    public void sendMess(){

        SendMail.sendEmail(Mail.buildMail(this.user.getAddress(),"网盘密码找回邮件","你的网盘账号为:" + user.getUsername()
                + ",密码为:" + user.getPassword() + " 请妥善保管"));
    }

    public String getTokenTag() {
        return tokenTag;
    }

    public void setTokenTag(String tokenTag) {
        this.tokenTag = tokenTag;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
