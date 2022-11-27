package com.virtualpuffer.netdisk.controller;

import com.virtualpuffer.netdisk.service.impl.file.FileBaseService;
import com.virtualpuffer.netdisk.utils.StringUtils;
import com.virtualpuffer.onlineChat.chat.HttpSessionConfigurator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.ServerEndpoint;
import java.io.FileNotFoundException;

@RestController
@ServerEndpoint(value = "/webSocketFile")
public class WebSocketController{
    @ResponseBody
    @RequestMapping(value = "/static/**")
    public void getRemoteResource(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String path = StringUtils.filePathDeal(request.getServletPath().substring("/webSocketFile/static/image".length()));
            FileBaseService service = FileBaseService.getInstance(path,5);
            service.downloadFile(response.getOutputStream());
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        }
    }
}
