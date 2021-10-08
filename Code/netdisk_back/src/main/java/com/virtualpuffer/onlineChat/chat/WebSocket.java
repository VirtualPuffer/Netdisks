package com.virtualpuffer.onlineChat.chat;

import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.entity.ChatResponseMessage;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.netdiskFile.ChatMap;
import com.virtualpuffer.netdisk.service.impl.user.UserServiceImpl;
import com.virtualpuffer.netdisk.service.impl.user.UserTokenService;
import com.virtualpuffer.netdisk.utils.Log;
import com.virtualpuffer.netdisk.utils.MybatisConnect;
import org.apache.ibatis.session.SqlSession;
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
    public UserServiceImpl service;
    private static CopyOnWriteArraySet<WebSocket> webSocketSet=new CopyOnWriteArraySet<>();
    private static LinkedList<String> messageList = new LinkedList<>();
    private static HashMap<Integer,WebSocket> hashMap = new HashMap<>();
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
            systemMessage("认证成功，账号为" + tokenService.getUser().getUsername());
        } catch (Exception e) {
            systemMessage("认证失败，当前无token");
        }
        webSocketSet.add(this);
    }
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);
        String name = service.getUser().getName();
        systemMessage("   " + name + "  断开连接");
    }
    @OnMessage
    public void onMessage(String message){
        System.out.println(message);
        broadCastMessage(message);
    }

    public void privateMessage(String message,String target){

    }

    public void broadCastMessage(String message){
        broadCastMessage(message,true);
    }

    public void systemMessage(String message){
        broadCastMessage(message,false);
    }

    private void broadCastMessage(String messageContent,boolean isCache){
        String message = null;
        ChatResponseMessage responseMessage = null;
        try {
            String name = service.getUser().getName();
            responseMessage = new ChatResponseMessage(Log.getTime(),name,messageContent);
        } catch (NullPointerException e) {
            responseMessage = new ChatResponseMessage(Log.getTime(),null,messageContent);
        }
        message = JSON.toJSONString(responseMessage);

        if (isCache) {
            messageCache(message);
        }
        for(WebSocket webSocket:webSocketSet){
            try {
                sendMessage(message,webSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message,WebSocket socket) throws IOException {
        socket.session.getBasicRemote().sendText(message);
    }

    public void messageCache(String message){
        SqlSession session = null;
        session = MybatisConnect.getSession();
        session.getMapper(ChatMap.class)
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
