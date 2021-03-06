package com.virtualpuffer.onlineChat.chat;

import org.springframework.stereotype.Component;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

@Component
public class HttpSessionConfigurator extends ServerEndpointConfig.Configurator {

     @Override
     public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
         sec.getUserProperties().put("request", request);
         sec.getUserProperties().put("response",response);
    }

}