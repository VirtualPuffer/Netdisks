package com.virtualpuffer.netdisk.startup;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketEndpointConfig {
    @Bean
    public ServerEndpointExporter getEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
