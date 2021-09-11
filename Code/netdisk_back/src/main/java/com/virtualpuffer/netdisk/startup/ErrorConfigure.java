package com.virtualpuffer.netdisk.startup;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 *错误捕捉
 */
@Configuration
public class ErrorConfigure implements ErrorPageRegistrar {

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage[] errorPages = new ErrorPage[3];
        errorPages[0] = new ErrorPage(HttpStatus.NOT_FOUND, "/index.html");
        errorPages[2] = new ErrorPage(HttpStatus.BAD_GATEWAY,"/error.html");
        errorPages[1] = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error.html");
        registry.addErrorPages(errorPages);
    }
}

