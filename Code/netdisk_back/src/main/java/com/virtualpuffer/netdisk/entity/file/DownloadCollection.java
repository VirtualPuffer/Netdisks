package com.virtualpuffer.netdisk.entity.file;

public class DownloadCollection{
    private Boolean preview = false;
    private String[] files;
    private String destination;
    private Integer second;
    private String key;
    private boolean getRandom;

    public DownloadCollection() {
    }

    public Boolean isPreview() {
        return preview;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public String[] getFiles() {
        return files;
    }

    public void setFiles(String[] files) {
        this.files = files;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isGetRandom() {
        return getRandom;
    }

    public void setGetRandom(boolean getRandom) {
        this.getRandom = getRandom;
    }
}
