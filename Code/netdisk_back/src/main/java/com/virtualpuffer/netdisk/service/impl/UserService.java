package com.virtualpuffer.netdisk.service.impl;

import com.virtualpuffer.netdisk.MybatisConnect;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.UserMap;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service("userDetailsService")
@Component
@Configuration
public class UserService implements UserDetailsService {
    @Autowired
    PasswordEncoder defaultEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SqlSession session = MybatisConnect.getSession();
        User user = null;
        try {
            user = session.getMapper(UserMap.class).getUserByUsername(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException(username);
        }
        List<GrantedAuthority> lisc = AuthorityUtils.commaSeparatedStringToAuthorityList("asda");
        return new org.springframework.security.core.userdetails.User(user.getUsername(),defaultEncoder.encode("123"),lisc);
    }

}
