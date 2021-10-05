package com.virtualpuffer.netdisk.controller;

import com.virtualpuffer.netdisk.controller.base.BaseController;
import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.service.impl.file.FileBaseService;
import com.virtualpuffer.netdisk.service.impl.file.FileTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

@Controller
@RestController
public class StraightDownloadController extends BaseController {
    @ResponseBody
    @RequestMapping(value = "/download/{token}")
    public ResponseMessage test(@PathVariable String token,String key, HttpServletResponse response){
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
    @RequestMapping(value = "/download/key/{token}")
    public Object i(@PathVariable String token,String key, HttpServletResponse response) throws IOException {
        if(key == null || key.equals("")){
                return new ModelAndView("/getURL.html");
            }else {
                InputStream inputStream = null;
                try {
                    FileBaseService fileService = FileTokenService.getInstanceByToken(token,key);
                    response.setContentType("application/force-download");
                    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileService.getNetdiskFile().getFile_Name(), "UTF-8"));
                    int length = (int) fileService.downloadFile(response.getOutputStream());
                    response.setContentLength(length);
                    return ResponseMessage.getSuccessInstance(200,"下载成功",null);
                }catch (ExpiredJwtException e){
                    return ResponseMessage.getExceptionInstance(300,"链接已失效",null);
                }catch (IllegalArgumentException e){
                    return ResponseMessage.getExceptionInstance(300,"密码错误",null);
                }catch (JwtException e){
                    return ResponseMessage.getExceptionInstance(300,"链接错误 : " + e.getMessage(),null);
                }catch (Exception e){
                    e.printStackTrace();
                    return ResponseMessage.getErrorInstance(500,"下载失败",null);
                }finally {
                    close(inputStream);
                }
            }
    }
}
