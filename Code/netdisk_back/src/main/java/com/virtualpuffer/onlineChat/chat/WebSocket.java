package com.virtualpuffer.onlineChat.chat;

import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.service.impl.user.UserServiceImpl;
import com.virtualpuffer.netdisk.service.impl.user.UserTokenService;
import com.virtualpuffer.netdisk.utils.Log;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint(value = "/webSocket",configurator = HttpSessionConfigurator.class)
public class WebSocket {
    private Session session;
    private static CopyOnWriteArraySet<WebSocket> webSocketSet=new CopyOnWriteArraySet<>();
    private static LinkedList<String> messageList = new LinkedList<>();
    private static HashMap<Integer,WebSocket> hashMap = new HashMap<>();
    public UserServiceImpl service;
    @OnOpen
    public void onOpen(Session session, EndpointConfig config){
        this.session=session;
        HandshakeRequest request = (HandshakeRequest)config.getUserProperties().get("request");
        try {
            String token =  request.getHeaders().get("Sec-WebSocket-Protocol").get(0);
            UserTokenService tokenService = UserTokenService.getInstanceByToken(token,"");
            this.service = tokenService;
            int id = tokenService.getUser().getUSER_ID();
            hashMap.put(id,this);
            printMessage(this);
            sendMessage("认证成功，账号为" + tokenService.getUser().getUsername());
        } catch (Exception e) {
            sendMessage("认证失败，当前无token");
        }
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
                try {
                    String name = service.getUser().getName();
                    webSocket.session.getBasicRemote().sendText(Log.getTime() + " : " + name + "   说：   " +message);
                    messageCache(Log.getTime() + " : " + name + "   说：   " +message);
                } catch (NullPointerException e) {
                    webSocket.session.getBasicRemote().sendText(Log.getTime() + " : "  + "  " +message);
                    messageCache(Log.getTime() + " : "  + "  " +message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void messageCache(String message){
        if(messageList.size() < 10){
            messageList.addLast(message);
        }else {
            messageList.addLast(message);
            messageList.removeFirst();
        }
    }
    public void printMessage(WebSocket socket) throws IOException {
        Iterator<String> iterator = messageList.iterator();
        while (iterator.hasNext()){
            socket.session.getBasicRemote().sendText(iterator.next());
        }
    }
}
