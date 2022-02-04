package com.virtualpuffer.netdisk.service.impl;

import com.virtualpuffer.netdisk.utils.Log;
import com.virtualpuffer.netdisk.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

@Async
@Service
public abstract class BaseServiceImpl {
    protected static final int BUFFER_SIZE = 4 * 1024;
    protected static final String properties = "getMess.properties";
    protected static final String ChineseProperties = "ChineseMess.properties";
    public static final String secretKey = "c7fp2dh6msk0";
    @Autowired
    public Log log;
    private static Properties get;
    private static Properties property;

    @Autowired
    protected RedisUtil redisUtil;

    static {
        InputStreamReader reader = null;
        if(property == null){
            try {
                property = new Properties();
                reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(ChineseProperties), "UTF-8");
                property.load(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                close(reader);
            }
        }
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(properties);
        if(get == null){
            try {
                get = new Properties();
                get.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                close(in);
            }
        }
    }

    /**
     * 文件、文件流复制
     * */
    protected static void copy(InputStream inputStream,OutputStream outputStream)throws IOException{
        byte[] buffer = new byte[BUFFER_SIZE];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
    }


    protected static void close(Closeable cos){
        try {
            if(cos!=null){
                cos.close();
            }
        } catch (IOException e) {
        }
    }

    protected static Timestamp getTimestamp(){
        return new Timestamp(System.currentTimeMillis());
    }

    protected static String getChineseProperties(String source){
        return property.getProperty(source);

    }    /*
    * 读取配置文件信息
    * */
    protected static String getMess(String source) {
        return get.getProperty(source);
    }

    public static String createToken(long time,Map<String,Object> claims,String subject){
        return createToken(time,claims,subject,secretKey);
    }

    /**
     * @param claims 加密集合
     * @param time 有效时间（秒）
     * @param subject 拥有者
     * @param key 密钥
    * */
    public static String createToken(long time,Map<String,Object> claims,String subject,String key) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        String currentKey = secretKey;//默认密钥
        if(key!=null){
            currentKey = key;
        }
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        JwtBuilder builder = Jwts.builder()
               .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setSubject(subject)
                .signWith(signatureAlgorithm,currentKey);
        if (time >= 0) {
            long expMillis = nowMillis + time * 1000;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }
    /**
     * 可以不传key,默认空密钥
     *
     *
     * */
    public static Claims parseJWT(String token,String key) {
        Claims claims = null;
        if (key == null) {
            claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } else {
            claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
        }
        return claims;
    }
    public static InputStream[] copyStream(InputStream inputStream){
        ByteArrayOutputStream baos = cloneInputStream(inputStream);
        InputStream[] ret = {new ByteArrayInputStream(baos.toByteArray())
                ,new ByteArrayInputStream(baos.toByteArray())};
        return ret;
    }

    private static ByteArrayOutputStream cloneInputStream(InputStream input) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    protected  static int getMessByInt(String source){
        return Integer.parseInt(getMess(source));
    }
    protected  static boolean getMessByBoolean(String source){
        return Boolean.getBoolean(getMess(source));
    }

    public void setRedisUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }
}
