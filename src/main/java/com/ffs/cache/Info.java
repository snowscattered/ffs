package com.ffs.cache;

import com.ffs.po.User;

public class Info {
    public long startTime;
    public long lastTime;
    public User user;

    public Info(User user) {
        startTime = lastTime = System.currentTimeMillis();
        this.user = user;
    }
}