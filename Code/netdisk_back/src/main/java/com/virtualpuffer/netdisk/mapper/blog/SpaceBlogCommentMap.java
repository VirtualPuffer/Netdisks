package com.virtualpuffer.netdisk.mapper.blog;

import java.sql.Timestamp;

public interface SpaceBlogCommentMap {
    int addThumb(int comment_id,int number);
    int deleteComment(int comment_id);
    int makeComment(String contentText, int reply_comment_id, int blog_id, int USER_ID, Timestamp time);
}
