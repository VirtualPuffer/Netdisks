package com.virtualpuffer.netdisk.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtualpuffer.netdisk.Security.securityFilter.LoginAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
@Configuration
@EnableWebSecurity
public class NetdiskSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAt(new LoginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
      /*  http.httpBasic().authenticationEntryPoint((request,response,authException) -> {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            PrintWriter out = response.getWriter();
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("code",403);
            map.put("message","未登录");
            out.write(new ObjectMapper().writeValueAsString(map));
            out.flush();
            out.close();
        });*/
        http
            .authorizeRequests()
                .antMatchers( "/signup", "/about").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .successForwardUrl("/cc")
                .permitAll();
            http.csrf().disable();


    }
    public JsonAuthenticationFilter

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(getEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers( "/signup", "/about");
    }

    @Bean("defaultEncoder")
    PasswordEncoder getEncoder(){return new BCryptPasswordEncoder(); }
}
