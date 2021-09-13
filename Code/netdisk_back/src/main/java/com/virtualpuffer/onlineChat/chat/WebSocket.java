package com.virtualpuffer.onlineChat.chat;

import com.virtualpuffer.netdisk.service.impl.user.UserTokenService;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint(value = "/webSocket",configurator = HttpSessionConfigurator.class)
public class WebSocket {
    private Session session;
    private static CopyOnWriteArraySet<WebSocket> webSocketSet=new CopyOnWriteArraySet<>();
    private static HashMap<Integer,WebSocket> hashMap = new HashMap<>();
    @OnOpen
    public void onOpen(Session session, EndpointConfig config){
        HandshakeRequest request = (HandshakeRequest)config.getUserProperties().get("request");

        try {
            String token =  request.getHeaders().get("Authorization").get(0);
            UserTokenService tokenService = UserTokenService.getInstanceByToken(token,"");
            int id = tokenService.getUser().getUSER_ID();
            hashMap.put(id,this);
            sendMessage("认证成功，账号为" + tokenService.getUser().getUsername());
        } catch (Exception e) {
            sendMessage("认证失败，当前无token");
        }
        this.session=session;
        webSocketSet.add(this);
    }
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);
        System.out.println("新断开");
    }
    @OnMessage
    public void onMessage(String message){
        System.out.println(message);
        sendMessage(message);
    }
    public void sendMessage(String message){
        for(WebSocket webSocket:webSocketSet){
            try {
                webSocket.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
