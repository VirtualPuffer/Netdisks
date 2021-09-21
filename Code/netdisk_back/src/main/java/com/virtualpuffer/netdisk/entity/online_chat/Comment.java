package com.virtualpuffer.netdisk.entity.online_chat;

import com.virtualpuffer.netdisk.Security.securityFilter.BaseFilter;
import com.virtualpuffer.netdisk.entity.BaseEntity;
import com.virtualpuffer.netdisk.entity.User;

public class Comment extends BaseEntity {
    public int user_id;
    public int thumb;//点赞
    public String time;
    public int blog_id;
    private int comment_id;
    public int replyComment_id;
    public String contentText;



}
