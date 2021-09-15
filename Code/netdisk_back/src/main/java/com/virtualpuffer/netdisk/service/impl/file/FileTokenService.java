package com.virtualpuffer.netdisk.service.impl.file;

import com.virtualpuffer.netdisk.entity.AbsoluteNetdiskFile;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.UserMap;
import com.virtualpuffer.netdisk.service.ParseToken;
import com.virtualpuffer.netdisk.utils.MybatisConnect;
import org.apache.ibatis.session.SqlSession;
import org.springframework.scheduling.annotation.Async;

import java.io.FileNotFoundException;
import java.util.Map;

@Async
public class FileTokenService extends FileHashService implements ParseToken {
    public FileTokenService(AbsoluteNetdiskFile netdiskFile, User user) throws FileNotFoundException {
        super(netdiskFile, user);
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
        try {
            Map map = parseJWT(token,key);
            if(map.get("hash") == null){
                session = MybatisConnect.getSession();
                String path = (String) map.get("path");
                int userID = (Integer) map.get("userID");
                User user = session.getMapper(UserMap.class).getUserByID(userID);
                AbsoluteNetdiskFile netdiskFile = new AbsoluteNetdiskFile(path);
                return new FileTokenService(netdiskFile,user);
            }else {
                String hash = (String) map.get("hash");
                String name = (String) map.get("name");
                return  getInstanceByHash(hash,name);
            }
        } finally {
            close(session);
        }
    }
}
