package com.virtualpuffer.onlineChat.SSL;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    //监听线程
    @Override
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(10004);
            Socket socket = null;
            while (true){
                socket = serverSocket.accept();
                new Thread(new ServerThread(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
