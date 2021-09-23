package com.virtualpuffer.netdisk.controller.spaceController;


import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.entity.online_chat.Blog;
import com.virtualpuffer.netdisk.entity.online_chat.Comment;
import com.virtualpuffer.netdisk.entity.online_chat.SpaceAttribute;
import com.virtualpuffer.netdisk.enums.Accessible;
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
    public ResponseMessage buildBlog(String content_text, Accessible access, HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        User user = loginService.getUser();
        if(access == null){
            access = Accessible.PUBLIC;
        }
        try {
            AbstractPersonalSpace space = new AbstractPersonalSpace(user);
            int blog_id = BlogService.buildBlog(user);
            space.getBlogService(blog_id).buildBlog(content_text,access);
            return ResponseMessage.getSuccessInstance(200,"动态发布成功",space.getAllBlog());
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseMessage.getErrorInstance(500, exception.getMessage(),null);
        }
    }
    @ResponseBody
    @RequestMapping(value = "/deleteBlog",method = RequestMethod.POST)
    public ResponseMessage deleteBlog(@RequestBody Blog blog, HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        User user = loginService.getUser();
        try {
            AbstractPersonalSpace space = new AbstractPersonalSpace(user);
            space.getBlogService(blog.getBlog_id()).delete();
            return ResponseMessage.getSuccessInstance(200,"动态删除成功",null);
        }catch (NullPointerException e){
            e.printStackTrace();
            return ResponseMessage.getExceptionInstance(400,"没有操作权限 : " + e.getMessage(),null);
        }catch (RuntimeException e){
            e.printStackTrace();
            return ResponseMessage.getExceptionInstance(400,e.getMessage(),null);
        }catch (Exception exception) {
            exception.printStackTrace();
            return ResponseMessage.getErrorInstance(500, exception.getMessage(),null);
        }
    }
    @ResponseBody
    @RequestMapping(value = "/getSpace/{name}",method = RequestMethod.GET)
    public ResponseMessage getSpaceBlog(@PathVariable String name, HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        try {
            User user = loginService.getUser();
            AbstractPersonalSpace space = new AbstractPersonalSpace(user,name);
            return ResponseMessage.getSuccessInstance(200,"动态获取成功",space.getAllBlog());
        } catch (RuntimeException e) {
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        } catch (Exception e) {
            return ResponseMessage.getErrorInstance(500,e.getMessage(),null);
        }
    }
    @ResponseBody
    @RequestMapping(value = "/getComment/{username}/{blog_id}",method = RequestMethod.GET)
    public ResponseMessage getComment(@PathVariable String username,@PathVariable int blog_id,
                                      HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        try {
            User user = loginService.getUser();
            AbstractPersonalSpace space = new AbstractPersonalSpace(user,username);
            Map map = space.getBlogService(blog_id).getCommentMap();
            return ResponseMessage.getSuccessInstance(200,"评论获取成功",map);
        } catch (RuntimeException e) {
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        } catch (Exception e) {
            return ResponseMessage.getErrorInstance(500,e.getMessage(),null);
        }
    }
    @ResponseBody
    @RequestMapping(value = "/addComment/{username}/{blog_id}",method = RequestMethod.GET)
    public ResponseMessage addComment(@RequestBody Comment comment,@PathVariable String username,
                                      @PathVariable int blog_id, HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        try {
            User user = loginService.getUser();
            AbstractPersonalSpace space = new AbstractPersonalSpace(user,username);
            space.getBlogService(blog_id);
            Map map = space.getBlogService(blog_id).getCommentMap();
            return ResponseMessage.getSuccessInstance(200,"评论获取成功",map);
        } catch (RuntimeException e) {
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        } catch (Exception e) {
            return ResponseMessage.getErrorInstance(500,e.getMessage(),null);
        }
    }

}
