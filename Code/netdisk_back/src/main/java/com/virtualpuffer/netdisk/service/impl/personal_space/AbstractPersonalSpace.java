package com.virtualpuffer.netdisk.service.impl.personal_space;

import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.entity.online_chat.Blog;
import com.virtualpuffer.netdisk.entity.online_chat.SpaceAttribute;
import com.virtualpuffer.netdisk.mapper.blog.SpaceBlogMap;
import com.virtualpuffer.netdisk.mapper.blog.SpaceMap;
import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;
import com.virtualpuffer.netdisk.utils.MybatisConnect;
import org.apache.ibatis.session.SqlSession;

import java.util.LinkedList;
import java.util.Set;

public abstract class AbstractPersonalSpace extends BaseServiceImpl {
    protected User user;
    protected SpaceAttribute spaceAttribute;
    protected LinkedList<Blog> blogList;
    protected Set<Photo_Album> photoSet;

    public AbstractPersonalSpace(User user) {
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            this.spaceAttribute = session.getMapper(SpaceMap.class).getSpaceProperties(user.getUSER_ID());
            this.blogList = session.getMapper(SpaceBlogMap.class).getAllBlog(user.getUSER_ID());
        } finally {
            close(session);
        }
    }


}
