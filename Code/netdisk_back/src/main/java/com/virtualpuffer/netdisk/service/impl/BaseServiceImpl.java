package com.virtualpuffer.netdisk.service.impl;

import com.virtualpuffer.netdisk.utils.Log;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;


@Service
public class BaseServiceImpl {
    protected static final int BUFFER_SIZE = 4 * 1024;
    protected static String properties = "getMess.properties";
    private static final String secretKey = "c7fp2dh6msk0";
    public static final Log errorLog = Log.getLog();

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
    //token解码
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
    public static InputStream[] copyStream(InputStream inputStream) throws FileNotFoundException {
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
}
