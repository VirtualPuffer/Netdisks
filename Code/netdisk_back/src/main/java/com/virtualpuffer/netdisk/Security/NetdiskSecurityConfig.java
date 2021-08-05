package com.virtualpuffer.netdisk.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableWebSecurity
public class NetdiskSecurityConfig extends WebSecurityConfigurerAdapter {
    private UserDetailsService userDetailsService;
/*    private TokenManager tokenManager;*/
    private DefaultPasswordEncoder encoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("83274285982");
        http
            .authorizeRequests()
                .antMatchers("/aa", "/signup", "/about").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .successForwardUrl("www.baidu.com")
                .loginPage("/login").permitAll();
            http.csrf().disable();


    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/aa", "/signup", "/about");
    }
}
