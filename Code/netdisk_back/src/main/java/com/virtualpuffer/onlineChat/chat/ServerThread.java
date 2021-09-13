package com.virtualpuffer.onlineChat.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.LinkedList;

/**
 * 该类为多线程类，用于服务端
 */
public class ServerThread implements Runnable {

    private Socket client = null;
    public ServerThread(Socket client){
        System.out.println("构造" + client);
        socketMap.add(client);
        this.client = client;
    }
    public static LinkedList<Socket> socketMap = new LinkedList<>();

    @Override
    public void run() {
        try{
                //获取Socket的输入流，用来接收从客户端发送过来的数据
            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
                boolean flag = true;
                while (flag) {
                    //接收从客户端发送过来的数据
                    String str = buf.readLine();
                    if (str == null || "".equals(str)) {
                        flag = false;
                    } else {
                        if ("bye".equals(str)) {
                            flag = false;
                        } else {
                            //将接收到的字符串前面加上echo，发送到对应的客户端
                            for(Socket socket : socketMap) {
                                System.out.println(socket + "容量测试");
                                System.out.println(socketMap.size() + "__________4324892890_________________");
                                PrintStream out = new PrintStream(socket.getOutputStream());
                                System.out.println("客户端说：" + str);
                                out.println("echo:" + str);
                            }
                        }
                    }
                }
                System.out.println("close一次");
                client.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
