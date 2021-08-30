package com.virtualpuffer.netdisk.service.impl;

import com.virtualpuffer.netdisk.MybatisConnect;
import com.virtualpuffer.netdisk.mapper.FileMap;
import com.virtualpuffer.netdisk.service.impl.file.FileServiceImpl;
import org.apache.ibatis.session.SqlSession;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;

public class GCService extends BaseServiceImpl{
    public static void gc(){
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            LinkedList<String> list = session.getMapper(FileMap.class).gc();
            if(!list.isEmpty()){
                Iterator<String> iterator = list.iterator();
                while (iterator.hasNext()){
                    String hash = iterator.next();
                    try {
                        FileServiceImpl impl = FileServiceImpl.getInstanceByHash(hash,"");
                        impl.getNetdiskFile().getFile().delete();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            close(session);
        }
    }
}
