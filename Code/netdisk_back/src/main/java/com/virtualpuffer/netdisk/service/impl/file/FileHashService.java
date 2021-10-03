package com.virtualpuffer.netdisk.service.impl.file;

import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskDirectory;
import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskEntity;
import com.virtualpuffer.netdisk.utils.MybatisConnect;
import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskFile;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.netdiskFile.FileMap;
import org.apache.ibatis.session.SqlSession;
import org.springframework.scheduling.annotation.Async;

import java.io.FileNotFoundException;

//@Async
public class FileHashService extends FileBaseService {
    public FileHashService(AbsoluteNetdiskEntity netdiskEntity, User user) throws FileNotFoundException {
        super(netdiskEntity, user);
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
                session.getMapper(FileMap.class).buildFileMap(this.file.getName(),hash,this.user.getUSER_ID(),this.netdiskDirectory.getDirectory_ID());
                session.commit();
            }else {
                throw new RuntimeException("hash not exit,please upload the native file");
            }
        } finally {
            close(session);
        }
    }
}
