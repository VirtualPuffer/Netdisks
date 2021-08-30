package com.virtualpuffer.netdisk.service.impl.user;

import com.virtualpuffer.netdisk.entity.BaseEntity;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.UserMap;
import com.virtualpuffer.netdisk.service.ParseToken;
import org.apache.ibatis.session.SqlSession;

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
        UserTokenService service = (UserTokenService)getInstance((String)map.get("username"),(String)map.get("password"),false , null);
        service.setTokenTag((String) map.get("tokenTag"));
        return service;
    }

    public static UserServiceImpl getInstanceByToken(String token, BaseEntity entity) {
        return getInstanceByToken(token,((User)entity).getIp());
    }
    public void resetPassword(String password){
        SqlSession session = null;
        try {
            if(tokenTag.equals("")){
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
