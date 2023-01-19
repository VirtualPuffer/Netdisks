package com.virtualpuffer.onlineChat.chat;

import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;
import com.virtualpuffer.netdisk.service.impl.user.UserServiceImpl;
import com.virtualpuffer.netdisk.service.impl.user.UserTokenService;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;

/**
 * 该类为多线程类，用于服务端
 */

public class AppSocket extends BaseServiceImpl implements Runnable{

    public static Set<AppSocket> socketSet = new HashSet<>();
    public Socket socket = null;
    public int connectTag = 0;
    public static UserTokenService service = null;
    public PrintStream out = null;
    private static final Class threadLock = ServerThread.class;
    private static final PrintStream systemStream = System.out;
    public static final String CONNECTTEST_REQUEST = "ping";
    public static final String CONNECTTEST_RESPONSE = "pong";
    public static final String DISCONNECT_REQUEST = "bye";
    public static final String DISCONNECT_RESPONSE = "byebye111";
    public AppSocket(Socket client){
        this.socket = client;
    }

    public void print(String string){
        out.print(string);
    }

    @Override
    public void run() {
        int tryTime = 0;
        String str = null;
        boolean permit = true;
        try {
            out = new PrintStream(socket.getOutputStream());
            BufferedReader buf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (permit) {
                String token = buf.readLine();
                System.out.println(token);
                try {
                    service = UserTokenService.getInstanceByToken(token, "");
                    System.out.println(service);
                    if(service!=null){
                        permit = false;//结束
                        socketSet.add(this);
                    }
                } catch (Exception e) {
                    if (tryTime > 2) {
                        return;
                    } else if (CONNECTTEST_REQUEST.equals(token)) {
                        out.println(CONNECTTEST_RESPONSE);
                    } else {
                        tryTime++;
                        out.println("Error");
                    }
                }
            }
            socket.setSoTimeout(10000);
            out.println("欢迎回来，连接已建立，通信地址：" + socket.getRemoteSocketAddress());
            WebSocket.printMessage(out);
            boolean listen = true;
            while (listen && !socket.isClosed()){
                String message = null;
                try {
                    message = buf.readLine();
                    if(CONNECTTEST_REQUEST.equals(message)){
                        out.println(DISCONNECT_RESPONSE);
                        connectTag = 0;
                    }else {
                        System.out.println("message : "+message);
                        WebSocket.broadCast(message,true,"message",service.getUser());
                    }
                } catch (SocketTimeoutException e){
                    if(connectTag < 3){
                        out.println(ServerThread.CONNECTTEST_RESPONSE);//res
                        connectTag ++;
                    }else {
                        out.println(DISCONNECT_RESPONSE);
                        return;
                    }
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (Exception exception){
        }finally {
            close(out);
            close(socket);
        }
    }
}
