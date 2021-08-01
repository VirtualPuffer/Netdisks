package com.virtualpuffer.netdisk.mapper;

public interface LoginHistory {
    int insertIntoHistory(long time);
    int deleteFromVerify(long time);
}
