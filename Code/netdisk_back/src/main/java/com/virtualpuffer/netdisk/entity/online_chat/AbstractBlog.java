package com.virtualpuffer.netdisk.entity.online_chat;

import com.virtualpuffer.netdisk.entity.BaseEntity;
import com.virtualpuffer.netdisk.entity.User;

public abstract class AbstractBlog extends BaseEntity {
    public int user_id;
    public int thumb;//点赞
    public String contentText;
}
