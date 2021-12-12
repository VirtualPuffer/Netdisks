package com.virtualpuffer.onlineChat.chat;

import com.virtualpuffer.netdisk.startup.NetdiskConfigure;
import com.virtualpuffer.netdisk.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;


@Component
public class Server extends Thread {

    @Autowired
    Log log;

    private Thread thread;

    @Value("${spring.socket.port}")
    private int socketPort;

    @Autowired
    public void start(){
        log.systemLog("端口监听启动");
        this.thread = new Thread(this);
        this.thread.start();
        //System.out.println("端口"+ socketPort +"监听启动");
    }

    @Override
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(socketPort);
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
