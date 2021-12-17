package com.virtualpuffer.onlineChat.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Client {
    private static int tag = 0;
    public static void main(String[] args) throws Exception {
        Thread nowThread = Thread.currentThread();
        Socket client = null;
        try {
            client = new Socket("47.96.253.99", 10004);
            //client = new Socket("127.0.0.1", 10004);
        } catch (IOException e) {
            if (tag < 3) {
                Thread.sleep(1000);
                System.out.println("连接失败，尝试重连");
                tag++;
                main(args);
            }else {
                System.out.println("连接失败，程序结束");
                System.exit(0);
            }
        }
        client.setSoTimeout(10000);
        new Out(client,nowThread).start();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        PrintStream out = new PrintStream(client.getOutputStream());
        while(Out.runnable){
            String str = input.readLine();
            out.println(str);
            if(ServerThread.DISCONNECT_REQUEST.equals(str)){
                Thread.sleep(1000);
            }
        }
    }
}
class Out extends Thread{
    Socket client;
    Thread mainThread;
    int connectTag = 0;
    public static boolean runnable = true;

    public Out(Socket socket,Thread mainThread) {
        this.mainThread = mainThread;
        this.client = socket;
    }

    @Override
    public void run() {
        BufferedReader buf = null;
        PrintStream out = null;
        //获取流
        try {
            out = new PrintStream(client.getOutputStream());
            buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while(runnable){
                try{
                    String echo = buf.readLine();//获取数据
                  if(ServerThread.DISCONNECT_RESPONSE.equals(echo) || echo == null){
                        runnable = false;
                        client.close();
                    }else if(ServerThread.CONNECTTEST_REQUEST.equals(echo)){
                        out.println(ServerThread.CONNECTTEST_RESPONSE);//响应心跳包
                    }else if(ServerThread.CONNECTTEST_RESPONSE.equals(echo)){
                        connectTag = 0;
                    }else {
                        System.out.println(echo);
                    }
                } catch (IOException e) {
                    if(connectTag < 3){
                        out.println(ServerThread.CONNECTTEST_REQUEST);
                        connectTag ++;
                    }else {
                        out.println(ServerThread.DISCONNECT_REQUEST);
                        return;
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            System.out.println("连接断开，程序结束");
            System.exit(0);
        }
    }
}
