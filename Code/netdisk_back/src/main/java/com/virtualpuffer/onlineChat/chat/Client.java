package com.virtualpuffer.onlineChat.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Client {
    public static void main(String[] args) throws Exception {
        Socket client = new Socket("47.96.253.99", 10004);
        client.setSoTimeout(10000);
        new Out(client).start();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        PrintStream out = new PrintStream(client.getOutputStream());
        while(Out.runnable){
            String str = input.readLine();
            out.println(str);
            if(ServerThread.DISCONNECT_REQUEST.equals(str)){
                Thread.sleep(3000);
            }
        }
    }
}
class Out extends Thread{
    public static boolean runnable = true;
    Socket client;
    public Out(Socket socket) {
        this.client = socket;
    }

    @Override
    public void run() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader buf = null;
        try {
            buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                e.printStackTrace();
            }
        }
    }
}
