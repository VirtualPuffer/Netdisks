package com.virtualpuffer.netdisk;

import com.virtualpuffer.netdisk.service.messageService.SendMail;
import com.virtualpuffer.netdisk.service.proxy.LoginServiceProxy;
import com.virtualpuffer.netdisk.utils.DemoFactory;
import com.virtualpuffer.netdisk.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@ServletComponentScan
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class},scanBasePackages = {"com.virtualpuffer.netdisk.controller","com.virtualpuffer.netdisk.service","com.virtualpuffer.netdisk.Security","com.virtualpuffer.netdisk.utils"})//核心包
public class NetdiskApplication {
    @Autowired
    RedisUtil redisUtil;

    public static void main(String[] args)throws Exception {
        SpringApplication.run(NetdiskApplication.class, args);
        Thread get = new Thread(new SendMail());
        get.start();

    }

}
