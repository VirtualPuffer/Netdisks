package com.virtualpuffer.onlineChat.chat;

import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.LinkedList;

/**
 * 该类为多线程类，用于服务端
 */
public class ServerThread extends BaseServiceImpl implements Runnable{

    private static final PrintStream systemStream = System.out;
    public static Socket socket = null;
    private Socket client = null;
    public ServerThread(Socket client){
        socket = client;
        this.client = client;
    }

    @Override
    public void run() {
        PrintStream out = null;
        try{
            out = new PrintStream(socket.getOutputStream());
            System.setOut(out);
            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
            boolean flag = true;
            while (flag) {
                //接收从客户端发送过来的数据
                String str = buf.readLine();
                if ("bye".equals(str)) {
                    out.println("byebye111");
                    return;
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (Exception exception){

        }finally {
            System.setOut(systemStream);
            close(out);
        }
    }

}
