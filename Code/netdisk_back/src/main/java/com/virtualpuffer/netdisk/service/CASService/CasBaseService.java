package com.virtualpuffer.netdisk.service.CASService;

import com.virtualpuffer.netdisk.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CasBaseService {
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
