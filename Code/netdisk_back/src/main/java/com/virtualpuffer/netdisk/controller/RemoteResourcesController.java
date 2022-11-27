package com.virtualpuffer.netdisk.controller;

import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.service.impl.file.FileBaseService;
import com.virtualpuffer.netdisk.service.impl.file.FileTokenService;
import com.virtualpuffer.netdisk.utils.StringUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.net.URLEncoder;

@Controller
@RestController
@RequestMapping(value = "/resource")
public class RemoteResourcesController {
    public static final String remoteCookie = "REMOTE_AU";
    public void getResource(){}

    @ResponseBody
    @RequestMapping(value = "/download/{token}")
    public ResponseMessage test(@PathVariable String token, String key, HttpServletResponse response){
        try {
            FileTokenService fileService = FileTokenService.getInstanceByToken(token,key);
            String fileName = fileService.getPackageName();
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "atetOutputStream());\n" +
                    "            response.setContentLength(length);tachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            int length = (int) fileService.download(response.getOutputStream());
            return null;
        }catch (ExpiredJwtException e){
            return ResponseMessage.getExceptionInstance(300,"链接已失效",null);
        }catch (JwtException e){
            return ResponseMessage.getExceptionInstance(300,"链接已错误 : " + e.getMessage(),null);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseMessage.getErrorInstance(500,"下载失败",null);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/static/**")
    public void getRemoteResource(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String path = StringUtils.filePathDeal(request.getServletPath().substring("/resource/static".length()));
            FileBaseService service = FileBaseService.getInstance(path,5);
            if(path.endsWith("html")){
                response.setHeader("Content-Type","text/html;charset=utf-8");
            }
            service.downloadFile(response.getOutputStream());
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        }
    }
}
