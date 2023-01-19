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
public class AppServer extends Thread {

    @Autowired
    Log log;

    private Thread thread;

    @Value("${spring.appSocket.port}")
    private int socketPort;

    private ServerSocket serverSocket;

    @Autowired
    public void start(){
        log.systemLog("app端口监听启动");
        this.thread = new Thread(this);
        this.thread.start();
        //System.out.println("端口"+ socketPort +"监听启动");
    }

    @Override
    public void run(){
        try {
            log.systemLog("端口监听：" + socketPort);
            serverSocket = new ServerSocket(socketPort);
            Socket socket = null;
            while (true){
                socket = serverSocket.accept();
                new Thread(new AppSocket(socket)).start();
            }
        } catch (IOException e) {
            try {
                serverSocket.close();
            } catch (Exception ioException) {
            }
            log.errorLog("后台端口异常：" + e.getMessage());
        }
    }
}
