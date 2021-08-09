package com.virtualpuffer.netdisk.mapper;

import java.sql.Timestamp;

public interface LoginHistory {
    int loginPersistence(int userID, String ip,Timestamp date,int tag);
    int insertIntoHistory(long time);
    int deleteFromVerify(long time);
}
