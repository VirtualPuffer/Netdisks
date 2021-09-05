package com.virtualpuffer.netdisk.service.impl.user;

import com.virtualpuffer.netdisk.MybatisConnect;
import com.virtualpuffer.netdisk.entity.BaseEntity;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.LoginHistory;
import com.virtualpuffer.netdisk.mapper.UserMap;
import com.virtualpuffer.netdisk.service.ParseToken;
import com.virtualpuffer.netdisk.utils.RedisUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * 分离出来的token认证，一般不做用户信息处理和变更，只用于验证
* */
public class UserTokenService extends UserServiceImpl implements ParseToken {

    @Autowired
    static RedisUtil redisUtil;

    public UserTokenService(User loginUser) {
        super(loginUser);
    }


    /**
     * 查看redis是否过期
     * */
    public static UserTokenService getInstanceByToken(String token, String ip) {
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            Map map = parseJWT(token,null);
            User user = session.getMapper(UserMap.class).userLogin((String)map.get("username"),(String)map.get("password"));

            if (user != null && !TOKEN_EXPIRE.equals(redisUtil.get(token))) {
                UserTokenService service = new UserTokenService(user);
                service.setTokenTag((String) map.get("tokenTag"));
                return service;
            }else {
                throw new RuntimeException("");
            }
        } finally {
            close(session);
        }
    }


    public static UserServiceImpl getInstanceByToken(String token, BaseEntity entity) {
        return getInstanceByToken(token,((User)entity).getIp());
    }

    public static void userLogout(String token){
        redisUtil.set(token,TOKEN_EXPIRE,UserServiceImpl.Time);
    }
    /**
     * 通过token保存账号密码
     * 通过账号密码注册对象
     * 如果密码已经被修改，将无法注册对象并抛出异常
     * 从而让链接只能修改一次密码
     * */
    public void resetPassword(String password){
        SqlSession session = null;
        try {
            if(tokenTag.equals(RESET_TAG)){
                session = MybatisConnect.getSession();
                int tag = session.getMapper(UserMap.class).resetPassword(password,user.getUSER_ID());
                if(tag == 1){
                    session.commit();
                }
            }else {
                throw new RuntimeException("密钥非法，接口调用失败");
            }
        } finally {
            close(session);
        }
    }
}
