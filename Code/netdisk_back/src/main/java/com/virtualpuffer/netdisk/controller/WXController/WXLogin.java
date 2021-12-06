package com.virtualpuffer.netdisk.controller.WXController;

import com.virtualpuffer.onlineChat.WXProgram.Connect;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/wx")
public class WXLogin {
    private static final int HASBEEN_USED = 40163;
    private static final int INVALID_CODE = 40029;
    @RequestMapping("login")
    public Object login(String code){
        Map<String,Object> map = Connect.getWxUserOpenid(code);
        if(map.get("code")!=null){
            int errcode = (Integer) map.get("errcode");
            if(errcode == HASBEEN_USED){
                return "已经用过了";
            }else if(errcode == INVALID_CODE){
                return "无效的CODE";
            }
        }
        if(map.get("openid") == null){
            return "获取失败";
        }else {
            return "获取成功：openid为" + (String)map.get("openid");
        }
    }
}
