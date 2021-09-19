package com.virtualpuffer.netdisk.entity.online_chat;

import com.virtualpuffer.netdisk.enums.Accessible;

public class SpaceAttribute {
    public int thumb;
    public Accessible accessible;

    public SpaceAttribute() {
    }

    public int getThumb() {
        return thumb;
    }

    public void setThumb(int thumb) {
        this.thumb = thumb;
    }

    public Accessible getAccessible() {
        return accessible;
    }

    public void setAccessible(Accessible accessible) {
        this.accessible = accessible;
    }
}
