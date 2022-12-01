package com.virtualpuffer.netdisk.startup;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GetHttpInfosConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        Map parameterMap = request.getParameterMap();
        sec.getUserProperties().putAll(parameterMap);
        sec.getUserProperties().put("request",request);
        super.modifyHandshake(sec, request, response);
    }
}