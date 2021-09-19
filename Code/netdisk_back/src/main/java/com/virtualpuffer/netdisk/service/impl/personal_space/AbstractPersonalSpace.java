package com.virtualpuffer.netdisk.service.impl.personal_space;

import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.entity.online_chat.Blog;

import java.util.LinkedList;
import java.util.Set;

public abstract class AbstractPersonalSpace {
    protected User user;
    protected LinkedList<Blog> blogList;
    protected Set<Photo_Album> photoSet;

}
