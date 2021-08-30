package com.virtualpuffer.netdisk.service.impl.file;

import com.virtualpuffer.netdisk.entity.NetdiskFile;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.service.ParseToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

public class FileTokenService extends FileHashService implements ParseToken {
    public FileTokenService(NetdiskFile netdiskFile, User user) throws FileNotFoundException {
        super(netdiskFile, user);
    }

    public FileTokenService() throws FileNotFoundException {
    }
    /**
     * 解析下载直链并转存
     * */
    public static FileTokenService getInstanceByURL(String destination,String url,User user) throws FileNotFoundException {
        if(url.substring(0,downloadAPI.length()).equals(downloadAPI)){
            FileTokenService service = getInstanceByToken(url.substring(downloadAPI.length()),null);
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
    public static FileTokenService getInstanceByToken(String token,String key) throws FileNotFoundException {
        Map map = parseJWT(token,key);
        if(map.get("hash") == null){
            String path = (String) map.get("path");
            int id = (Integer) map.get("userID");
            return (FileTokenService) getInstanceByPath(path,id);
        }else {
            String hash = (String) map.get("hash");
            String name = (String) map.get("name");
            return (FileTokenService) getInstanceByHash(hash,name);
        }
    }
}
