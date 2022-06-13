package com.virtualpuffer.netdisk.controller;

import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.service.impl.file.FileTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

@Controller
@RestController
@RequestMapping(value = "/resource")
public class RemoteResourcesController {

    public void getResource(){}

    @ResponseBody
    @RequestMapping(value = "/download/{token}")
    public ResponseMessage test(@PathVariable String token, String key, HttpServletResponse response){
        try {
            FileTokenService fileService = FileTokenService.getInstanceByToken(token,key);
            String fileName = fileService.getPackageName();
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            int length = (int) fileService.download(response.getOutputStream());
            response.setContentLength(length);
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
    @RequestMapping(value = "/resource/{path}")
    public void getRemoteResource(HttpServletRequest request,HttpServletResponse response,@PathVariable String path) throws Exception {
        try {
            //String path = StringUtils.filePathDeal(request.getServletPath().substring("/resource".length()));
            FileBaseService service = FileBaseService.getInstance(path,5);
            service.downloadFile(response.getOutputStream());
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        }
    }
}
