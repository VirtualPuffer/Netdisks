package com.virtualpuffer.onlineChat.chat;

import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;
import com.virtualpuffer.netdisk.utils.Log;
import com.virtualpuffer.netdisk.utils.StringUtils;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

/**
 * 该类为多线程类，用于服务端
 */
public class ServerThread extends BaseServiceImpl implements Runnable{
    private Socket client = null;
    public static Socket socket = null;
    public int connectTag = 0;
    private static final Class threadLock = ServerThread.class;
    private static final PrintStream systemStream = System.out;
    public static final String CONNECTTEST_REQUEST = "ping";
    public static final String CONNECTTEST_RESPONSE = "pong";
    public static final String DISCONNECT_REQUEST = "bye";
    public static final String DISCONNECT_RESPONSE = "byebye111";
    public static final String PASSWORD = getMess("sslpassword");
    public ServerThread(Socket client){
        this.client = client;
    }
    public void localCMD(String command,File workPath){}

    @Override
    public void run() {
        int tryTime = 0;
        String str = null;
        PrintStream out = null;
        boolean permit = true;

        synchronized (threadLock) {
            try {
                socket.close();
            } catch (Exception e) {
            }
            try {
                socket = this.client;
                socket.setSoTimeout(50000);
            } catch (SocketException e) {
            }
        }
        try {
            out = new PrintStream(socket.getOutputStream());
            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (permit) {
                out.print("password: ");
                String get = buf.readLine();
                if (PASSWORD.equals(get)) {
                    permit = false;
                } else if (tryTime > 2) {
                    return;
                } else if (CONNECTTEST_REQUEST.equals(get)) {
                    out.println(CONNECTTEST_RESPONSE);
                } else{
                    tryTime++;
                    out.println("please try again");
            }
        }
            System.setOut(out);
            socket.setSoTimeout(10000);
            out.println("欢迎回来，连接已建立，通信地址：" + socket.getRemoteSocketAddress());
            boolean flag = true;
            Process currentProcess = null;
            String currentPath = "/bin/sh";
            while (flag && !socket.isClosed()) {
                try {
                    str = "";
                    str = buf.readLine();
                } catch (SocketTimeoutException e){
                    if(connectTag < 3){
                        out.println(ServerThread.CONNECTTEST_REQUEST);
                        connectTag ++;
                    }else {
                        out.println(DISCONNECT_RESPONSE);
                        return;
                    }
                }
                if (DISCONNECT_REQUEST.equals(str) && client.equals(socket)) {
                    out.println(DISCONNECT_RESPONSE);
                    return;
                }else if(CONNECTTEST_REQUEST.equals(str)){
                    out.println(CONNECTTEST_RESPONSE);
                }else if(CONNECTTEST_RESPONSE.equals(str)){
                    connectTag = 0;
                }else if(str.startsWith("cd")){
                    String path = str.substring(2).trim();
                    String absolutePath = StringUtils.filePathDeal(path);
                    File x = new File(absolutePath);
                    if(x.exists()){
                        System.out.println(absolutePath + " t");
                        currentPath = absolutePath;
                        out.println("路径已经切换至："+ currentPath);
                    }else{
                        System.out.println(absolutePath+" f");
                        String relativePath = currentPath + "/" + StringUtils.filePathDeal(path);
                        File relative = new File(relativePath);
                        if(relative.exists()){
                            currentPath = relativePath;
                            out.println("路径已经切换至："+ currentPath);
                        }else {
                            out.println("路径："+ path +"不存在");
                        }

                    }
                }else if(str.equals(PASSWORD)){
                    if(currentProcess!=null &&currentProcess.isAlive()){
                        currentProcess.destroy();
                        currentProcess = null;
                    }
                }else if(str!=null && !str.equals("")){
                    if(currentProcess!=null&&!currentProcess.isAlive()){
                        currentProcess.destroy();
                        currentProcess = null;
                    }
                    if(currentProcess!=null){
                        if(currentProcess.isAlive()){
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.toString().getBytes(StandardCharsets.UTF_8));
                            copy(byteArrayInputStream,currentProcess.getOutputStream());
                        }
                    }else {
                        try{
                            String[] cmd = { currentPath, "-c", str.trim() };
                            Process process = Runtime.getRuntime().exec(cmd);
                            process.getOutputStream();
                            currentProcess = process;
                            copy(process.getInputStream(),out);
                        }catch (Exception e){
                            out.println(e.getMessage());
                        }
                    }
                }
            }
        } catch (IOException ioException) {
            log.errorLog(ioException.getMessage());
        } catch (Exception exception){
        }finally {
            System.setOut(systemStream);
            close(out);
        }
    }
}
