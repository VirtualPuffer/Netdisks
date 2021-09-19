package com.virtualpuffer.netdisk.mapper.user;

import java.sql.Timestamp;

public interface LoginHistory {
    int loginPersistence(int userID, String ip,Timestamp date,int tag,long clock);
    int insertIntoHistory(long time);
    int deleteFromVerify(long time);
}
