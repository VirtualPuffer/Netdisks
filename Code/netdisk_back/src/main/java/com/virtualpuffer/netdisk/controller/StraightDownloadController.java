package com.virtualpuffer.netdisk.controller;

import com.virtualpuffer.netdisk.controller.base.BaseController;
import com.virtualpuffer.netdisk.service.impl.FileServiceImpl;
import io.jsonwebtoken.JwtException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

@RestController
public class StraightDownloadController extends BaseController {
    @RequestMapping(value = "/download/{token}")
    public void test(@PathVariable String token, HttpServletResponse response)throws IOException{
        InputStream inputStream = null;
        try {
            FileServiceImpl fileService = FileServiceImpl.getInstanceByToken(token);
            inputStream = fileService.downloadFile(response.getOutputStream());
            response.setContentLength(inputStream.available());
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileService.getFile_name(), "UTF-8"));
            return;
        } /*catch (Exception A){
            response.sendRedirect("/Netdisk/api/aa");
        }*/catch (JwtException e){
            response.sendError(300,"链接已过期");
        }finally {
            close(inputStream);
        }
    }
}
