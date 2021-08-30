package com.virtualpuffer.netdisk.service.impl.user;

import com.virtualpuffer.netdisk.entity.BaseEntity;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.service.ParseToken;
import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;

import java.util.Map;

/**
 * 分离出来的token认证，一般不做用户信息处理和变更，只用于验证
* */
public class UserTokenService extends UserServiceImpl implements ParseToken {

    public UserTokenService(User loginUser) {
        super(loginUser);
    }

    public static UserTokenService getInstanceByToken(String token, String ip) {
        Map map = parseJWT(token,null);
        return (UserTokenService)getInstance((String)map.get("username"),(String)map.get("password"),false , null);
    }

    public static UserServiceImpl getInstanceByToken(String token, BaseEntity entity) {
        return getInstanceByToken(token,((User)entity).getIp());
    }
    public void resetPassword(String password){

    }
}
