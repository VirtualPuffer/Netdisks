package com.virtualpuffer.netdisk.service.CASService;

import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class CasBaseService extends BaseServiceImpl {
    private String type;
    private static final String secretKey = "c7fp2dh6msk0";

   /* @Bean("casService")*/
    public CasBaseService getInstance(){
        return new CasBaseService();
    }


    public Claims parseTicket(String token) {
        Claims claims = Jwts.parser()
                //设置签名的秘钥
                .setSigningKey(secretKey)
                //设置需要解析的jwt
                .parseClaimsJws(token).getBody();
        return claims;
    }


}
