package com.virtualpuffer.onlineChat.test1;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/webSocket")
public class WebSocket {
    private Session session;
    private static CopyOnWriteArraySet<WebSocket> webSocketSet=new CopyOnWriteArraySet<>();
    private static HashMap<Session,WebSocket> hashMap = new HashMap<>();
    @OnOpen
    public void onOpen(Session session){
        this.session=session;
        hashMap.put(session,this);
        webSocketSet.add(this);
        System.out.println("新连接");
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
        //log.info("【websocket消息】收到客户端发来的消息：{}",message);
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
