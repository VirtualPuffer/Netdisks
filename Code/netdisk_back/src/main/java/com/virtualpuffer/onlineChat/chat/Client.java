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
    public static boolean runnable = true;
    Socket client;
    Thread mainThread;
    public Out(Socket socket,Thread mainThread) {
        this.mainThread = mainThread;
        this.client = socket;
    }

    @Override
    public void run() {
        BufferedReader buf = null;
        try {
            buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while(runnable){
                try{
                    String echo = buf.readLine();
                    System.out.println(echo);
                    if(ServerThread.DISCONNECT_RESPONSE.equals(echo) || echo == null){
                        runnable = false;
                        client.close();
                    }
                }catch(SocketTimeoutException e){
                    //System.out.println("Time out, No response");
                } catch (IOException e) {
                }
            }
        } finally {
            System.exit(0);
        }
    }
}
