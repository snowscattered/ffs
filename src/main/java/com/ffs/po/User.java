package com.ffs.po;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * user 表的 POJO 类
 * @author hoshinosena
 * @version 1.1
 */
public class User {
    public int uid;
    public Role role;
    public String username;
    public String password;
    public String name;
    // 1.1添加了 image 字段用作用户头像
    public String image;
    public String tel;
    public String address;
    public String info;
    public Lock lock=new ReentrantLock();

    public boolean tryLock()
    {
        return lock.tryLock();
    }
    public void unlock()
    {
        lock.unlock();
    }
}
