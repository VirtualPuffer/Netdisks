package com.virtualpuffer.onlineChat.chat;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 易水●墨龙吟
 * @Description
 * @create 2019-04-14 23:40
 */

//@Component
public class TestRunner implements CommandLineRunner {

    @Autowired
    private SocketProperties properties;

    @Override
    public void run(String... args) throws Exception {
        ServerSocket server = null;
        Socket socket = null;
        server = new ServerSocket(properties.getPort());
        System.out.println("设备服务器已经开启, 监听端口:" + properties.getPort());
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                properties.getPoolCore(),
                properties.getPoolMax(),
                properties.getPoolKeep(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(properties.getPoolQueueInit()),
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );
        while (true) {
            socket = server.accept();
            new a(socket).start();
            System.out.println("接到一个");
        }
    }

    public void setProperties(SocketProperties properties) {
        this.properties = properties;
    }
    protected static void copy(InputStream inputStream, OutputStream outputStream)throws IOException{
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
    }
}
class a extends Thread{
    Socket socket;
    public a(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true){
            try {
               // InputStream inputStream =socket.getInputStream();
                byte[] buffer = new byte[1024];
                int length = 0;
              //  inputStream.read(buffer);
                System.out.println(buffer);
                System.out.println(socket.isConnected());
                OutputStream outputStream = socket.getOutputStream();
                System.out.println(1);
                outputStream.write(43243);
                System.out.println(2);
                outputStream.flush();
                System.out.println(3);
                sleep(1000);
                System.out.println(4);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}