package com.virtualpuffer.netdisk;

import com.virtualpuffer.netdisk.controller.UserController;
import com.virtualpuffer.netdisk.service.impl.user.UserTokenService;
import com.virtualpuffer.netdisk.service.messageService.SendMail;
import com.virtualpuffer.netdisk.startup.NetdiskContextWare;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class},scanBasePackages = {"com.virtualpuffer.netdisk.*"})//核心包
public class NetdiskApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(NetdiskApplication.class, args);
        Thread get = new Thread(new SendMail());
        get.start();
        UserController controller = NetdiskContextWare.getBean(UserController.class);
        System.out.println(controller.ki("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsb2NhbCIsInBhc3N3b3JkIjoiMTIxIiwidG9rZW5UYWciOiJyZXNldCIsImlwIjpudWxsLCJleHAiOjE2MzA5Mzk4ODAsInVzZXJJRCI6MiwiaWF0IjoxNjMwOTM5MjgwLCJqdGkiOiI1NTVhYTFmOS03NmVjLTQyZjQtYTJkYS0wYTVhZTViZGE4OWMiLCJ1c2VybmFtZSI6ImxvY2FsIn0.YIPM90nCT6gJKMuT3STcy6z68bADF6Ocr6GWoGGdXLg", null, null, null));
    }
}
