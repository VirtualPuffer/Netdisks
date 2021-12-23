package com.virtualpuffer.netdisk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;


@ServletComponentScan
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class},scanBasePackages = {"com.virtualpuffer.*"})//核心包
public class NetdiskApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(NetdiskApplication.class, args);
    }
}
