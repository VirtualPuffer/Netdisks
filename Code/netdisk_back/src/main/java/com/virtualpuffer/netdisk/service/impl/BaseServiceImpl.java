package com.virtualpuffer.netdisk.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@Service
public class BaseServiceImpl {
    protected static final int BUFFER_SIZE = 4 * 1024;
    protected static String properties = "getMess.properties";
    private static final String secretKey = "c7fp2dh6msk0";

    protected static void close(Closeable cos){
        try {
            if(cos!=null){
                cos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    * 读取配置文件信息
    * */
    protected static String getMess(String source) {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(properties);
        Properties get = new Properties();
        try {

            get.load(in);
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return get.getProperty(source);
    }
    public String createToken(long time,Map<String,Object> claims,String subject) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

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
    //token解码
    public static Claims parseJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }
    protected  static int getMessByInt(String source){
        return Integer.parseInt(getMess(source));
    }
    protected  static boolean getMessByBoolean(String source){
        return Boolean.getBoolean(getMess(source));
    }
}
