package com.virtualpuffer.netdisk.utils;

import java.util.concurrent.ThreadFactory;

public class NetdiskThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable demoInstance) {
        Thread thread = new Thread(demoInstance);
        thread.start();
        return thread;
    }
}
