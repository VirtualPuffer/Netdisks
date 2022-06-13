package com.virtualpuffer.netdisk.entity.file;

import com.virtualpuffer.netdisk.entity.BaseEntity;
import com.virtualpuffer.netdisk.utils.Message;
import org.apache.ibatis.session.SqlSession;

import java.io.Serializable;
import java.util.HashMap;

/**
* lockMap记录每个用户的锁，使得每个用户的操作都是同步的，保证数据安全
* */
public abstract class AbsoluteNetdiskEntity extends BaseEntity implements Serializable {
    protected int USER_ID;
    public static final String downloadAPI = Message.getMess("downloadAPI");//下载链接前缀
    public static final String defaultWare = Message.getMess("defaultWare");
    public static final String duplicateFileWare = Message.getMess("duplicateFileWare");
    public HashMap<Integer,Object> lockMap = new HashMap<>();

    public Object getLock(){
        if(lockMap.containsKey(USER_ID)){
            return lockMap.get(USER_ID);
        }else {
            Object lock = new Object();
            lockMap.put(USER_ID,lock);
            return lock;
        }
    }
    public abstract void rename(String name);

    public abstract void delete(SqlSession session);
}
