package com.virtualpuffer.onlineChat.chat;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author 易水●墨龙吟
 * @Description
 * @create 2019-04-18 22:35
 */
@Component
@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "spring.socket")
public class SocketProperties {
    private Integer port;
    private Integer poolKeep;
    private Integer poolCore;
    private Integer poolMax;
    private Integer poolQueueInit;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getPoolKeep() {
        return poolKeep;
    }

    public void setPoolKeep(Integer poolKeep) {
        this.poolKeep = poolKeep;
    }

    public Integer getPoolCore() {
        return poolCore;
    }

    public void setPoolCore(Integer poolCore) {
        this.poolCore = poolCore;
    }

    public Integer getPoolMax() {
        return poolMax;
    }

    public void setPoolMax(Integer poolMax) {
        this.poolMax = poolMax;
    }

    public Integer getPoolQueueInit() {
        return poolQueueInit;
    }

    public void setPoolQueueInit(Integer poolQueueInit) {
        this.poolQueueInit = poolQueueInit;
    }
}
