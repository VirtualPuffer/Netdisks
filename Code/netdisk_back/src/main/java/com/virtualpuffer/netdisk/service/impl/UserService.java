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
        LinkedList<User> list = session.getMapper(UserMap.class).getUserByUsername(username);
        if (list.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        User user = list.getFirst();
/*        list.getFirst().setPassword(defaultEncoder.encode(list.getFirst().getPassword()));*/

        List<GrantedAuthority> lisc = AuthorityUtils.commaSeparatedStringToAuthorityList("asda");

        System.out.println(user.getUsername());
        System.out.println(user.getPassword());
        System.out.println(lisc);
        return new org.springframework.security.core.userdetails.User(user.getUsername(),defaultEncoder.encode("123"),lisc);
    }

}
