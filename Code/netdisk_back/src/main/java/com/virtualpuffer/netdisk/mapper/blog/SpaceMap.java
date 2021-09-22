package com.virtualpuffer.netdisk.mapper.blog;

import com.virtualpuffer.netdisk.entity.online_chat.SpaceAttribute;
import com.virtualpuffer.netdisk.enums.Accessible;

public interface SpaceMap {
    SpaceAttribute getSpaceProperties(int USER_ID);
    int setSpaceProperties(SpaceAttribute attribute,int USER_ID);
    int setAccessible(Accessible accessible);

    int setSpaceAccess(String access,int USER_ID);
    int setSpaceBackground(String url,int USER_ID);
    //int setSpaceProperties(SpaceAttribute attribute,int USER_ID);
}
