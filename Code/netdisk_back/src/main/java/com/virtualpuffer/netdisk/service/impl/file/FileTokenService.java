package com.virtualpuffer.netdisk.service.impl.file;

import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskDirectory;
import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskEntity;
import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskFile;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.netdiskFile.FileMap;
import com.virtualpuffer.netdisk.mapper.user.UserMap;
import com.virtualpuffer.netdisk.service.ParseToken;
import com.virtualpuffer.netdisk.utils.MybatisConnect;
import org.apache.ibatis.session.SqlSession;
import org.springframework.scheduling.annotation.Async;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

//@Async
public class FileTokenService extends FileHashService implements ParseToken {
    public FileTokenService(AbsoluteNetdiskEntity netdiskEntity, User user) throws FileNotFoundException {
        super(netdiskEntity, user);
    }

    public FileTokenService() throws FileNotFoundException {
    }
    /**
     * 解析下载直链并转存
     * */
    public static FileBaseService getInstanceByURL(String destination, String url, User user) throws FileNotFoundException {
        if(url.substring(0,downloadAPI.length()).equals(downloadAPI)){
            FileBaseService service = getInstanceByToken(url.substring(downloadAPI.length()),null);
            service.setUser(user);
            service.netdiskFile.setFile_Destination(destination + "/" + service.netdiskFile.getFile_Name());
            return service;
        }else {
            throw new RuntimeException("url解析失败");
        }
    }
    /**
     * @param token 需要解析的token
     * 解析token里的FileService对象
     *
     * 没办法确定是hash还是path(不知道是不是文件)
     * 文件夹没办法给hash，只能给路径，被删了就没办法了
     * */
    public static FileBaseService getInstanceByToken(String token, String key) throws FileNotFoundException {
        SqlSession session = null;
        AbsoluteNetdiskDirectory netdiskDirectory = null;
        AbsoluteNetdiskFile netdiskFile = null;
        User user = null;
        try {
            session = MybatisConnect.getSession();
            Map map = parseJWT(token,key);
            int userID = (Integer) map.get("userID");
            Integer[] dirCollection = (Integer[]) map.get("dir_id");
            Integer[] fileCollection = (Integer[])map.get("file_id");
            user = session.getMapper(UserMap.class).getUserByID(userID);

            for (Integer dir_id : dirCollection) {
                netdiskDirectory = AbsoluteNetdiskDirectory.getInstance(dir_id);
                new FileTokenService(netdiskDirectory,user);
            }
            for(Integer file_id : fileCollection){
                netdiskFile = session.getMapper(FileMap.class).getFileByMapID(file_id);
                File file = new FileTokenService(netdiskFile,user).getFile();
            }
        } finally {
            close(session);
        }
    }
}
