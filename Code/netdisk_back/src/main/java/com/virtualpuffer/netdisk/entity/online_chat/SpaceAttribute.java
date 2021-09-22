package com.virtualpuffer.netdisk.entity.online_chat;

import com.virtualpuffer.netdisk.enums.Accessible;

public class SpaceAttribute {
    public int thumb;
    public Accessible access;
    public String backgroundPictureURL;

    public SpaceAttribute() {
    }

    public int getThumb() {
        return thumb;
    }

    public void setThumb(int thumb) {
        this.thumb = thumb;
    }

    public Accessible getAccess() {
        return access;
    }

    public void setAccessible(Accessible accessible) {
        this.access = access;
    }
}
