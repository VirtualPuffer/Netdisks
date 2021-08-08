package com.virtualpuffer.netdisk.service.impl;

import com.virtualpuffer.netdisk.MybatisConnect;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.UserMap;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.ibatis.session.SqlSession;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginService extends BaseServiceImpl {
    /**
    * 初始阶段只传入账号密码，登录状态
     * 解析token
     * 登录成功后完善内容
    *
    * */
    private User  user;
    private static final long time = 7*24*60*60;
    private static final String secretKey = "c7fp2dh6msk0";

    public LoginService(User loginUser){
        this.user = loginUser;
        user.setToken(createToken(time));
    }


    public static LoginService getInstance(String token) throws RuntimeException{
        Map map = parseJWT(token);
        return getInstance((String)map.get("username"),(String)map.get("password"));
    }
    public static LoginService getInstance(User user)throws RuntimeException{
        return getInstance(user.getUsername(),user.getPassword());
    }
    public static LoginService getInstance(String username,String password) throws RuntimeException{
        SqlSession session = MybatisConnect.getSession();
        User user = session.getMapper(UserMap.class).getUserByUsername(username,password);
        if(user == null ){
            throw new RuntimeException("用户名或者密码错误");
        }
        return new LoginService(user);
    }

    public String createToken(long time) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("userID", this.user.getUSER_ID());
        claims.put("username", this.user.getUsername());
        claims.put("password", this.user.getPassword());

        String subject = String.valueOf(user.getUSER_ID());

        JwtBuilder builder = Jwts.builder()
                //如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                //代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .setSubject(subject)
                //设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm,secretKey);
        if (time >= 0) {
            long expMillis = nowMillis + time * 1000;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }
    public static Claims parseJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
