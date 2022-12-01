package com.virtualpuffer.onlineChat.chat;

import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.entity.ChatResponseMessage;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskDirectory;
import com.virtualpuffer.netdisk.mapper.netdiskFile.ChatMap;
import com.virtualpuffer.netdisk.service.impl.file.FileBaseService;
import com.virtualpuffer.netdisk.service.impl.file.FileUtilService;
import com.virtualpuffer.netdisk.service.impl.user.UserServiceImpl;
import com.virtualpuffer.netdisk.service.impl.user.UserTokenService;
import com.virtualpuffer.netdisk.startup.GetHttpInfosConfigurator;
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
@ServerEndpoint(value = "/webSocket",configurator = GetHttpInfosConfigurator.class)
public class WebSocket {

    private Session session;
    public UserServiceImpl service;
    public static boolean singleThread = true;
    public static final boolean Single_Connect = true;
    private static CopyOnWriteArraySet<WebSocket> webSocketSet=new CopyOnWriteArraySet<>();
    private static LinkedList<String> messageList = new LinkedList<>();
    private static HashMap<Integer,WebSocket> hashMap = new HashMap<>();
    public static AtomicInteger message_id = null;//消息id类
    public static HashMap<WebSocket,Integer> Connect_Map = new HashMap<>();//心跳记录，停跳直接切断连接
    public static int disconnect_time = 3;//停跳次数
    public static final String PONG = "pongroccorrocccorpong";//响应包

    static {
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            LinkedList<ChatResponseMessage> list = session.getMapper(ChatMap.class).getLastMessage(100);
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
        for(String s : config.getUserProperties().keySet()){
            System.out.println(s + " " + config.getUserProperties().get(s));
        }
        String token =  request.getHeaders().get("Sec-WebSocket-Protocol").get(0);
        this.service = UserTokenService.getInstanceByToken(token,"");
        if(service == null){
            onClose();
            throw new RuntimeException("没登录进你妈聊天室");
        }
        if(Single_Connect){
            single_connect();//单连接限制
        }
        Connect_Map.put(this,0);
        printMessage(this);
        systemMessage(this.service.getUser().getName()+" 进入了聊天室");
        webSocketSet.add(this);
    }

    public void single_connect() throws Exception {
        try {
            int id = this.service.getUser().getUSER_ID();
            try {
                if(hashMap.containsKey(id)){
                    hashMap.get(id).onClose();
                }
            }catch (Exception e){}
            hashMap.put(id,this);
        }catch (Exception z){}
    }
    @OnClose
    public void onClose(){
        try {
            Connect_Map.remove(this);
            webSocketSet.remove(this);
            this.session.close();
            String name = service.getUser().getName();
            systemMessage("   " + name + "  断开连接");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @OnMessage
    public void onMessage(String message)throws Exception{
        if(PONG.equals(message)){
            Connect_Map.put(this,0);
        }else {
            broadCastMessage(message);
        }
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
                FileBaseService service = FileBaseService.getInstance("image",5, AbsoluteNetdiskDirectory.default_priviledge);
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

    public static void connectCheck(){
        for(WebSocket socket : Connect_Map.keySet()){
            int time = Connect_Map.get(socket);
            if(time <= disconnect_time){
                Connect_Map.put(socket,time+1);
            }else {
                try {
                    socket.onClose();
                    Connect_Map.remove(socket);
                }catch (Exception e){}
            }
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
                    sleep(10000);
                    WebSocket.connectCheck();
                    sleep(10000);
                } catch (InterruptedException e) {
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
