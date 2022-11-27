package com.virtualpuffer.netdisk.mapper.netdiskFile;

import com.virtualpuffer.netdisk.entity.ChatResponseMessage;

import java.sql.Timestamp;
import java.util.LinkedList;

public interface ChatMap {
    LinkedList<ChatResponseMessage> getLastMessage(int number);
    int sendMessage(Timestamp time,int USER_ID,String content,int target,int message_id);
}
