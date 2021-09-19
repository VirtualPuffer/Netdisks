package com.virtualpuffer.netdisk.service.impl.file;

import com.virtualpuffer.netdisk.utils.MybatisConnect;
import com.virtualpuffer.netdisk.entity.AbsoluteNetdiskFile;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.netdiskFile.FileMap;
import org.apache.ibatis.session.SqlSession;
import org.springframework.scheduling.annotation.Async;

import java.io.FileNotFoundException;

@Async
public class FileHashService extends FileBaseService {
    public FileHashService(AbsoluteNetdiskFile netdiskFile, User user) throws FileNotFoundException {
        super(netdiskFile, user);
    }
    public FileHashService() throws FileNotFoundException {
    }

    public static FileHashService getInstanceByHash(String hash, String name) throws FileNotFoundException {
        FileHashService service = new FileHashService();
        AbsoluteNetdiskFile netdiskFile = AbsoluteNetdiskFile.getInstance(hash,name);
        service.setNetdiskFile(netdiskFile);
        service.setFile(netdiskFile.getFile());
        return service;
    }

    public void upLoadByHash() throws Exception {
        SqlSession session = null;
        String dest = this.netdiskFile.getFile_Destination();
        String place = dest.substring(0,dest.lastIndexOf("/")+1);
        String hash = this.netdiskFile.getFile_Hash();
        try {
            if(checkDuplicate(hash)){
                session = MybatisConnect.getSession();
                session.getMapper(FileMap.class).buildFileMap(dest,this.file.getName(),hash,this.user.getUSER_ID(),place);
                session.commit();
            }else {
                throw new RuntimeException("hash not exit,please upload the native file");
            }
        } finally {
            close(session);
        }
    }
}
