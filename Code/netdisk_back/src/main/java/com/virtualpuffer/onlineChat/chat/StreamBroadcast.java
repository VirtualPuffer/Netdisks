package com.virtualpuffer.onlineChat.chat;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class StreamBroadcast{
    public HashMap<Integer,ClientMirror> clientMap = new HashMap<>();
    public HashMap<Integer,Integer> collectMap = new HashMap<>();

    public StreamBroadcast(){

    }

    public void addClient(ClientMirror clientMirror){
        clientMap.put(clientMirror.code,clientMirror);
    }



}
