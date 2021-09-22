package com.virtualpuffer.netdisk.service.impl.personal_space;

import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.entity.online_chat.Blog;
import com.virtualpuffer.netdisk.entity.online_chat.SpaceAttribute;
import com.virtualpuffer.netdisk.enums.Accessible;
import com.virtualpuffer.netdisk.mapper.blog.SpaceBlogMap;
import com.virtualpuffer.netdisk.mapper.blog.SpaceMap;
import com.virtualpuffer.netdisk.mapper.user.UserMap;
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
    protected boolean isHost;
    protected SpaceAttribute spaceAttribute;
    protected Map<Integer,Blog> blogMap;
    protected Set<Photo_Album> photoSet;

    //设置模式
    public AbstractPersonalSpace(User user) {
        SqlSession session = null;
        try {
            isHost = true;
            this.user = user;
            session = MybatisConnect.getSession();
            this.spaceAttribute = session.getMapper(SpaceMap.class).getSpaceProperties(user.getUSER_ID());
            this.blogMap = session.getMapper(SpaceBlogMap.class).getAllBlog(user.getUSER_ID());
        } finally {
            close(session);
        }
    }
    //访问模式
    public AbstractPersonalSpace(User user,String name)throws RuntimeException {
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            int id = session.getMapper(UserMap.class).getIDByName(name);
            isHost = (user.getUSER_ID() == id);
            this.spaceAttribute = session.getMapper(SpaceMap.class).getSpaceProperties(id);
            if(spaceAttribute.getAccess() == Accessible.PRIVATE && !isHost){
                throw new RuntimeException("该空间尚未开放");
            }
            if(isHost){
                this.user = session.getMapper(UserMap.class).getUserByID(id);
                this.blogMap = session.getMapper(SpaceBlogMap.class).getAllBlog(id);
            }else {
                this.blogMap = session.getMapper(SpaceBlogMap.class).getAllPublicBlog(id);
            }
        } finally {
            close(session);
        }
    }

    public SpaceAttribute getSpaceAttribute() {
        return spaceAttribute;
    }

    public void setSpaceAttribute(SpaceAttribute spaceAttribute) {
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            int count = 0;
            if (spaceAttribute.access!=null) {
                count+=session.getMapper(SpaceMap.class).setSpaceAccess(spaceAttribute.access.name(),user.getUSER_ID());
            }
            if (spaceAttribute.backgroundPictureURL!=null) {
                count+=session.getMapper(SpaceMap.class).setSpaceBackground(spaceAttribute.backgroundPictureURL,user.getUSER_ID());
            }
            if(count > 0) session.commit();
        } finally {
            close(session);
        }

    }

    public Map<Integer,Blog> getAllBlog(){
        return blogMap;
    }
    //保证是这个空间的
    public BlogService getBlogService(int blog_id){
        return new BlogService(blogMap.get(blog_id).getBlog_id(),isHost);
    }

}
