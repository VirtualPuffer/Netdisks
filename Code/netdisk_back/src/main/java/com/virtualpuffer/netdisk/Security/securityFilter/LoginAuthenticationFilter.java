package com.virtualpuffer.netdisk.Security.securityFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    public LoginAuthenticationFilter() throws AuthenticationException{
        super(new AntPathRequestMatcher("",""));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)
                || request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE))//解析请求头
        {
            ObjectMapper mapper = new ObjectMapper();
            UsernamePasswordAuthenticationToken authRequest = null;
            try (InputStream inputStream = request.getInputStream()) {
                Map<String,String> authenticationBean = mapper.readValue(inputStream, Map.class);
                authRequest = new UsernamePasswordAuthenticationToken(authenticationBean.get("username"), authenticationBean.get("password"));
            } catch (IOException e) {
                e.printStackTrace();
                authRequest = new UsernamePasswordAuthenticationToken("", "");
            } finally {
                setDetails(request, authRequest);
                return super.getAuthenticationManager().authenticate(authRequest);
            }
        }
        else {
            return super.attemptAuthentication(request, response);
        }
    }
}
