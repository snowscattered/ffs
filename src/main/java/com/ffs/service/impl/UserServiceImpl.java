package com.ffs.service.impl;

import com.ffs.mapper.UserMapper;
import com.ffs.po.Role;
import com.ffs.po.User;
import com.ffs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * UserServiceImpl 实现了 UserService
 * 详细信息在 UserService 接口中说明
 * @author hoshinosena
 * @version 1.1
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public User findUser(int uid) {
        return userMapper.selectUserByUid(uid);
    }

    @Override
    public User findUser(String username) {
        if ("".equals(username)) {
            return null;
        }

        return userMapper.selectUserByUsername(username);
    }

    @Override
    public List<User> findUsers(Role role) {
        return userMapper.selectUsersByRole(role.ordinal());
    }

    @Override
    public List<User> findUsers(String name) {
        if ("".equals(name)) {
            return new ArrayList<>();
        }

        return userMapper.selectUsersByName(name);
    }

    @Override
    public List<User> findUsers() {
        return userMapper.selectUsers();
    }

    @Override
    public int addUser(User user) {
        if (user.role == null) {
            return -1;
        } else if (user.username == null || "".equals(user.username)) {
            return -2;
        } else if (user.password == null || "".equals(user.password)) {
            return -3;
        } else if (user.name == null || "".equals(user.name)) {
            return -4;
        }
        // 1.1添加 image 的处理
        if (user.image == null) {
            user.image = "";
        }
        if (user.tel == null) {
            user.tel = "";
        }
        if (user.address == null) {
            user.address = "";
        }
        if (user.info == null) {
            user.info = "";
        }

        return userMapper.insertUser(user, user.role.ordinal());
    }

    @Override
    public int updUser(User user) {
        int role = user.role == null ? -1 : user.role.ordinal();
        if ("".equals(user.username)) {
            return -2;
        } else if ("".equals(user.password)) {
            return -3;
        } else if ("".equals(user.name)) {
            return -4;
        }

        return userMapper.updateUser(user, role);
    }

    @Override
    public int delUser(int uid) {
        return userMapper.deleteUser(uid);
    }
}
