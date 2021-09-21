package com.virtualpuffer.netdisk.controller;


import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.service.impl.personal_space.AbstractPersonalSpace;
import com.virtualpuffer.netdisk.service.impl.personal_space.BlogService;
import com.virtualpuffer.netdisk.service.impl.user.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/space")
public class SpaceController {
    @ResponseBody
    @RequestMapping(value = "/SendBlog",method = RequestMethod.POST)
    public void buildBlog(String content_text,HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        User user = loginService.getUser();
        try {
            AbstractPersonalSpace space = new AbstractPersonalSpace(user);
            int blog_id = BlogService.buildBlog(user);
            space.getBlogService(blog_id).buildBlog(content_text);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    @ResponseBody
    @RequestMapping(value = "/getSpace/{username}",method = RequestMethod.GET)
    public ResponseMessage getSpaceBlog(@PathVariable String username, HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        try {
            User user = loginService.getUser();
            AbstractPersonalSpace space = new AbstractPersonalSpace(user,username);
            return ResponseMessage.getSuccessInstance(200,"动态获取成功",space.getAllBlog());
        } catch (RuntimeException e) {
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        } catch (Exception e) {
            return ResponseMessage.getErrorInstance(500,e.getMessage(),null);
        }
    }
}
