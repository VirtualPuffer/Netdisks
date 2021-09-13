package com.virtualpuffer.onlineChat.test1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.ServerSocket;
import java.net.Socket;

@Component
public class Server implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(10004);
        Socket socket = null;
        while (true){
            socket = serverSocket.accept();
            new Thread(new ServerThread(socket)).start();
        }
    }
}
