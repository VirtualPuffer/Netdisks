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
import java.util.Map;
import java.util.Set;

/**
 * space对象通过user构造
 *blogService只能通过space对象获取
 *保证权限控制
* */
public class AbstractPersonalSpace extends BaseServiceImpl {
    protected User user;
    protected SpaceAttribute spaceAttribute;
    protected Map<Integer,Blog> blogMap;
    protected Set<Photo_Album> photoSet;

    public AbstractPersonalSpace(User user) {
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            this.spaceAttribute = session.getMapper(SpaceMap.class).getSpaceProperties(user.getUSER_ID());
            this.blogMap = session.getMapper(SpaceBlogMap.class).getAllBlog(user.getUSER_ID());
        } finally {
            close(session);
        }
    }
    public Map<Integer,Blog> getAllBlog(){
        return blogMap;
    }
    public BlogService getBlogService(int blog_id){
        return new BlogService(blogMap.get(blog_id));
    }

    /**
     * 创建之前没有id
     * 所以只能用静态方法创建并返回
    * */
    public Blog buildBlog(){
        return BlogService.buildBlog(this.user);
    }

}
