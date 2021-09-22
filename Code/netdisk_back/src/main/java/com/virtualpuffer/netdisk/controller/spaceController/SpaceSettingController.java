package com.virtualpuffer.netdisk.controller.spaceController;

import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.entity.online_chat.SpaceAttribute;
import com.virtualpuffer.netdisk.service.impl.personal_space.AbstractPersonalSpace;
import com.virtualpuffer.netdisk.service.impl.user.UserServiceImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api/space/set/")
public class SpaceSettingController {
    @ResponseBody
    @RequestMapping(value = "/setSpace")
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
}
