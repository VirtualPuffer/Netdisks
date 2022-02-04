package com.virtualpuffer.netdisk.service.RemoteResourcesService;

import com.virtualpuffer.netdisk.enums.ContentType;
import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;
import com.virtualpuffer.netdisk.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 自动解析token
 * token:下载地址、有效时长、下载次数（redis）、文件id（hashcode）
 * filetype在类装载的时候确定
* */
public class ResourceService extends BaseServiceImpl {
    @Autowired
    RedisTemplate redisTemplate;
    private static final String RESOURCE_WARE = getMess("resource_ware");
    public static  final String RESOURCE_TAG = "4";
    public String file_path;
    public String file_name;
    public ContentType file_type;

    public ResourceService(){}

    public ResourceService(String file_name, ContentType contentType){
        this.file_path = StringUtils.filePathDeal(RESOURCE_WARE+file_name);
        this.file_type = contentType;
    }

    public static ResourceService getInstanceByToken(String token){
        BaseServiceImpl.parseJWT(token,null);
        return new ResourceService();
    }

    public String getDownloadURL(long mills){
        Map<String,Object> map = new HashMap<>();
        map.put("file_name",file_name);
        map.put("file_type",file_type.content_type);
        createToken(mills,map,null);
        return "";
    }

    public String getOnlineURL(){
        return "";
    }

    public void download(OutputStream outputStream) throws IOException {
        File file = new File(file_path);
        copy(new FileInputStream(file),outputStream);
    }


}
