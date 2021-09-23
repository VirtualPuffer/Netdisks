package com.virtualpuffer.netdisk.controller.spaceController;

import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.entity.online_chat.SpaceAttribute;
import com.virtualpuffer.netdisk.enums.Accessible;
import com.virtualpuffer.netdisk.service.impl.personal_space.AbstractPersonalSpace;
import com.virtualpuffer.netdisk.service.impl.user.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/space/")
public class SpaceSettingController {
    @ResponseBody
    @RequestMapping(value = "/setSpaceAttribute")
    public ResponseMessage getComment(@RequestBody SpaceAttribute attribute, HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        try {
            User user = loginService.getUser();
            AbstractPersonalSpace space = new AbstractPersonalSpace(user);
            space.setSpaceAttribute(attribute);
            return ResponseMessage.getSuccessInstance(200,"设置成功",null);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        } catch (Exception e) {
            return ResponseMessage.getErrorInstance(500,e.getMessage(),null);
        }
    }
    @ResponseBody
    @RequestMapping(value = "/getAttribute/{name}")
    public ResponseMessage getAttribute(@PathVariable String name,HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        try {
            User user = loginService.getUser();
            AbstractPersonalSpace space = new AbstractPersonalSpace(user,name);
            SpaceAttribute attribute = space.getSpaceAttribute();
            Map ret = new HashMap();
            ret.put("attribute",attribute);
            return ResponseMessage.getSuccessInstance(200,"配置获取成功",ret);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        } catch (Exception e) {
            return ResponseMessage.getErrorInstance(500,e.getMessage(),null);
        }
    }
}
