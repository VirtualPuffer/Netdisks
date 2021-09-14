package com.virtualpuffer.onlineChat.chat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
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
