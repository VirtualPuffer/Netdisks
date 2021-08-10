package com.virtualpuffer.netdisk.Security;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;

public class SecurityRequest extends RequestFacade {
    public SecurityRequest(Request request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        return super.getParameter(name);
    }
}
