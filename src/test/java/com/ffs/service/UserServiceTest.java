package com.ffs.service;

import com.ffs.po.Role;
import com.ffs.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * UserService 的测试方法
 * @author hoshinosena
 * @version 1.0
 */
@SpringBootTest
public class UserServiceTest {
    @Autowired
    UserService userService;

    @Test
    public void test() throws Exception {
        List<User> users = userService.findUsers();
        for (int i=users.size()-1; 0<=i; i--) {
            userService.delUser(users.get(i).uid);
        }

        addUser();
        findUser();
        updUser();
        delUser();
    }

    public void addUser() throws Exception {
        User user = new User();

        if (userService.addUser(user) != -1) {
            throw new Exception();
        }
        user.role = Role.admin;
        if (userService.addUser(user) != -2) {
            throw new Exception();
        }
        user.username = "username";
        if (userService.addUser(user) != -3) {
            throw new Exception();
        }
        user.password = "password";
        if (userService.addUser(user) != -4) {
            throw new Exception();
        }
        user.name = "name";
        if (userService.addUser(user) != 1) {
            throw new Exception();
        }
    }

    public void findUser() throws Exception {
        if (userService.findUser("username") == null) {
            throw new Exception();
        } else if (userService.findUsers(Role.admin) == null) {
            throw new Exception();
        } else if (userService.findUsers("name") == null) {
            throw new Exception();
        } else if (userService.findUsers().size() != 1) {
            throw new Exception();
        }
        User user = userService.findUser("username");
        if (!Role.admin.equals(user.role)) {
            throw new Exception();
        } else if (!"username".equals(user.username)) {
            throw new Exception();
        } else if (!"password".equals(user.password)) {
            throw new Exception();
        } else if (!"name".equals(user.name)) {
            throw new Exception();
        } else if (!"".equals(user.tel)) {
            throw new Exception();
        } else if (!"".equals(user.address)) {
            throw new Exception();
        } else if (!"".equals(user.info)) {
            throw new Exception();
        }
    }

    public void updUser() throws Exception {
        User user = userService.findUser("username");
        user.name = "";
        if (userService.updUser(user) != -4) {
            throw new Exception();
        }
        user.password = "";
        if (userService.updUser(user) != -3) {
            throw new Exception();
        }
        user.username = "";
        if (userService.updUser(user) != -2) {
            throw new Exception();
        }
        user.username = "test username";
        user.password = null;
        user.name = "test name";
        user.tel = "test tel";
        user.address = "test address";
        user.info = "test info";
        if (userService.updUser(user) != 1) {
            throw new Exception();
        }
        user = userService.findUser("test username");
        if (!Role.admin.equals(user.role)) {
            throw new Exception();
        } else if (!"password".equals(user.password)) {
            throw new Exception();
        } else if (!"test name".equals(user.name)) {
            throw new Exception();
        } else if (!"test tel".equals(user.tel)) {
            throw new Exception();
        } else if (!"test address".equals(user.address)) {
            throw new Exception();
        } else if (!"test info".equals(user.info)) {
            throw new Exception();
        }
        user.role = Role.buyer;
        if (userService.updUser(user) != 1) {
            throw new Exception();
        }
        user.password = "test password";
        if (userService.updUser(user) != 1) {
            throw new Exception();
        }
        user = userService.findUser("test username");
        if (!Role.buyer.equals(user.role)) {
            throw new Exception();
        } else if (!"test password".equals(user.password)) {
            throw new Exception();
        }
    }

    public void delUser() throws Exception {
        userService.delUser(userService.findUser("test username").uid);
        if (userService.findUsers().size() != 0) {
            throw new Exception();
        }
    }
}
