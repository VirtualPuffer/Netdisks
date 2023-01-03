package com.virtualpuffer.onlineChat.chat;

import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;
import com.virtualpuffer.netdisk.service.impl.user.UserServiceImpl;
import com.virtualpuffer.netdisk.service.impl.user.UserTokenService;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Locale;

/**
 * 该类为多线程类，用于服务端
 */

public class AppSocket extends BaseServiceImpl implements Runnable{
    public static void main(String[] args) {

    }
    private Socket client = null;
    public static Socket socket = null;
    public int connectTag = 0;
    public static UserTokenService service = null;
    private static final Class threadLock = ServerThread.class;
    private static final PrintStream systemStream = System.out;
    public static final String CONNECTTEST_REQUEST = "ping";
    public static final String CONNECTTEST_RESPONSE = "pong";
    public static final String DISCONNECT_REQUEST = "bye";
    public static final String DISCONNECT_RESPONSE = "byebye111";
    public AppSocket(Socket client){
        this.client = client;
    }

    @Override
    public void run() {
        int tryTime = 0;
        String str = null;
        PrintStream out = null;
        boolean permit = true;

        synchronized (threadLock) {
            try {
                socket.close();
            } catch (Exception e) {
            }
            try {
                socket = this.client;
                socket.setSoTimeout(50000);
            } catch (SocketException e) {
            }
        }
        try {
            out = new PrintStream(socket.getOutputStream());
            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.println("password");
            while (permit) {
                String token = buf.readLine();
                try {
                    service = UserTokenService.getInstanceByToken(token, "");
                    if(service!=null){
                        permit = false;
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
            boolean listen = true;
            while (listen){
                String message = buf.readLine();
                WebSocket.broadCast(message,false,"message",service.getUser());
            }
            out.println("欢迎回来，连接已建立，通信地址：" + socket.getRemoteSocketAddress());
            boolean flag = true;
            File current = new File("/bin/sh");
            while (flag && !socket.isClosed()) {
                Runtime.getRuntime().exec("");
                try {
                    str = buf.readLine();
                } catch (SocketTimeoutException e){
                    if(connectTag < 3){
                        out.println(ServerThread.CONNECTTEST_REQUEST);
                        connectTag ++;
                    }else {
                        out.println(DISCONNECT_RESPONSE);
                        return;
                    }
                }

                if (DISCONNECT_REQUEST.equals(str) && client.equals(socket)) {
                    out.println(DISCONNECT_RESPONSE);
                    return;
                }else if(CONNECTTEST_REQUEST.equals(str)){
                    out.println(CONNECTTEST_RESPONSE);
                }else if(CONNECTTEST_RESPONSE.equals(str)){
                    connectTag = 0;
                }else if(str.startsWith("$cd")){
                    String path = str.substring(3).trim();
                    File file = new File(path);
                    if(file.exists()){
                        current = file;
                    }
                }else if(str.startsWith("$")){
                    try{
                        InputStream inputStream = Runtime.getRuntime().exec(str.substring(1),null,current).getInputStream();
                    }catch (Exception e){

                    }
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (Exception exception){
        }finally {
            System.setOut(systemStream);
            close(out);
        }
    }
}
