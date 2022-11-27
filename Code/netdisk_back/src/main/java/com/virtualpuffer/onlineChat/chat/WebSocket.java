package com.virtualpuffer.onlineChat.chat;

import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.entity.ChatResponseMessage;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.netdiskFile.ChatMap;
import com.virtualpuffer.netdisk.service.impl.file.FileBaseService;
import com.virtualpuffer.netdisk.service.impl.file.FileUtilService;
import com.virtualpuffer.netdisk.service.impl.user.UserServiceImpl;
import com.virtualpuffer.netdisk.service.impl.user.UserTokenService;
import com.virtualpuffer.netdisk.startup.NetdiskContextWare;
import com.virtualpuffer.netdisk.utils.Log;
import com.virtualpuffer.netdisk.utils.MybatisConnect;
import com.virtualpuffer.netdisk.utils.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint(value = "/webSocket",configurator = HttpSessionConfigurator.class)
public class WebSocket {

    private Session session;
    public UserServiceImpl service;
    public static boolean singleThread = true;
    private static CopyOnWriteArraySet<WebSocket> webSocketSet=new CopyOnWriteArraySet<>();
    private static LinkedList<String> messageList = new LinkedList<>();
    private static HashMap<Integer,WebSocket> hashMap = new HashMap<>();

   public static AtomicInteger message_id = null;//消息id类

    static {
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            LinkedList<ChatResponseMessage> list = session.getMapper(ChatMap.class).getLastMessage(10);
            try {
                message_id = new AtomicInteger(list.getFirst().getMessage_id());
            }catch (NoSuchElementException e){
                message_id = new AtomicInteger(0);
            }
            for(ChatResponseMessage responseMessage: list){
                messageList.addFirst(responseMessage.getContent());
            }
        } finally {
            session.close();
        }
    }
    public WebSocket(){
    }
    @OnOpen
    public void onOpen(Session session, EndpointConfig config)throws Exception{
        if(singleThread){
            singleThread = false;
            Thread thread = new ConnectMaintain();
            thread.start();
        }
        this.session=session;
        HandshakeRequest request = (HandshakeRequest)config.getUserProperties().get("request");
        try {
            String token =  request.getHeaders().get("Sec-WebSocket-Protocol").get(0);
            UserTokenService tokenService = UserTokenService.getInstanceByToken(token,"");
            this.service = tokenService;
            int id = tokenService.getUser().getUSER_ID();
            hashMap.put(id,this);
            printMessage(this);
            systemMessage(tokenService.getUser().getName()+" 进入了聊天室");
        } catch (Exception e) {
            systemMessage("匿名用户 进入了聊天室");
        }
        webSocketSet.add(this);
    }
    @OnClose
    public void onClose()throws Exception{
        webSocketSet.remove(this);
        String name = service.getUser().getName();
        systemMessage("   " + name + "  断开连接");
    }
    @OnMessage
    public void onMessage(String message)throws Exception{
        broadCastMessage(message);
    }

    public void privateMessage(String message,String target){

    }

    public void pingMessage(String message)throws Exception{
        broadCastMessage(message,false,"ping");
    }

    public void broadCastMessage(String message)throws Exception{
        broadCastMessage(message,true,"message");
    }

    public void systemMessage(String message)throws Exception{
        broadCastMessage(message,false,"log");
    }
    private void broadCastMessage(String messageContent,boolean isCache,String type) throws Exception {
        //处理base64编码
        if(messageContent.startsWith("data:image")){
            String SHA = FileUtilService.getSH256(messageContent);
            String path = "image/" + SHA;
            try {
                FileBaseService service = FileBaseService.getInstance("image",5);
                service.setFile(new File(StringUtils.filePathDeal(path)));
                service.uploadFile(FileUtilService.getStringInputStream(messageContent));
            } catch (Exception e) {
            }
            messageContent = "rochttp://47.96.253.99/Netdisk/resource/static/image/" + SHA;
        }
        String message = null;
        ChatResponseMessage responseMessage = null;
        int current_id = isCache ? message_id.incrementAndGet() : 0;//线程安全获取ID
        try {
            String name = service.getUser().getName();
            responseMessage = new ChatResponseMessage(Log.getTime(),name,messageContent,current_id);
        } catch (NullPointerException e) {
            responseMessage = new ChatResponseMessage(Log.getTime(),"匿名用户",messageContent,current_id);
        }
        responseMessage.setType(type);
        message = JSON.toJSONString(responseMessage);
        if (isCache) {
            messageCache(message,current_id);
        }
        for(WebSocket webSocket:webSocketSet){
            try {
                sendMessage(message,webSocket);
            } catch (IOException e) {
            }
        }
    }

    public void sendMessage(String message,WebSocket socket) throws IOException {
        socket.session.getBasicRemote().sendText(message);
    }

    public void messageCache(String message,int message_id){
        SqlSession session = null;
        try{
            session = MybatisConnect.getSession();
            try {
                session.getMapper(ChatMap.class).sendMessage(new Timestamp(System.currentTimeMillis()),service.getUser().getUSER_ID(),message,-1,message_id);
            }catch (NullPointerException e){
                session.getMapper(ChatMap.class).sendMessage(new Timestamp(System.currentTimeMillis()),11,message,-1,message_id);
            }
            session.commit();
            if(messageList.size() < 10){
                messageList.addLast(message);
            }else {
                messageList.addLast(message);
                messageList.removeFirst();
            }
        }finally {
            session.close();
        }
    }
    public void printMessage(WebSocket socket) throws IOException {
        Iterator<String> iterator = messageList.iterator();
        while (iterator.hasNext()){
            socket.session.getBasicRemote().sendText(iterator.next());
        }
    }
}

class ConnectMaintain extends Thread{
    WebSocket webSocket = NetdiskContextWare.getBean(WebSocket.class);
    public void run() {
        while (true){
            if(webSocket!=null){
                try {
                    webSocket.pingMessage("ping");
                    sleep(20000);
                } catch (InterruptedException e) {
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
