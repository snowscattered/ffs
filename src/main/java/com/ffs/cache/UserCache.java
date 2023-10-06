package com.ffs.cache;

import com.ffs.po.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserCache {
    public long time;
    public Map<Integer, User> cache;

    public UserCache() {
        time = System.currentTimeMillis();
        cache = new ConcurrentHashMap<>();
    }
}