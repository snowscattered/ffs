package com.ffs.controller.User;

import com.ffs.cache.UserCache;
import com.ffs.controller.Generation.BaseParameter;
import com.ffs.po.Role;
import com.ffs.po.User;
import com.ffs.service.UserService;
import com.ffs.util.helper.Assign;
import com.ffs.util.helper.PageChunk;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * UserAPI 处理 user 表的增删改查请求
 * 仅 POST 方法
 * @author hoshinosena
 * @version 1.0
 */
@Controller
@RequestMapping("${baseURL}" + "api/user/")
public class UserAPI {
    @Value("${selectBlockSize}")
    int blockSize;
    @Autowired
    UserService userService;

    public static User preprocessingToken(BaseParameter p, Map<String, Object> r) {
        User user;

        if (p.token == null || "".equals(p.token)) {
            r.put("code", -1);
            r.put("message", "失去连接");
            return null;
        }
        user = UserCache.getUser(p.token);
        if (user == null) {
            r.put("code", -1);
            r.put("message", "失去连接");
        }

        return user;
    }

    private boolean lockUser(User user) {
        // 获取锁
        // 超过10次返回失败
        for (int i=0; i<10; i++) {
            if (user.tryLock()) {
                return true;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}
        }

        return false;
    }

    private void unlockUser(User user) {
        user.unlock();
    }

    @PostMapping("/get")
    @ResponseBody
    public Object getUser(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = preprocessingToken(p, r);
        if (user == null) {
            return r;
        }
        // 非管理员查询逻辑
        // 只能查询 uid(自己) 或商户
        // 查询优先级 uid > name
        if (!Role.admin.equals(user.role)) {
            // 查询所有商户
            if (p.uid == p.name) {
                if (p.role != null && !"shop".equals(p.role)) {
                    r.put("code", -11023);
                    r.put("message", "不正确的身份");
                    return r;
                }
                PageInfo<User> pageInfo = PageChunk.chunk(p.block, blockSize, new Assign<>() {
                    @Override
                    public List<User> assign() {
                        return userService.findUsers(Role.shop);
                    }
                });
                r.put("code", 0);
                r.put("message", "success");
                r.put("users", pageInfo.getList());
                r.put("block", pageInfo.getPageNum());
                r.put("blocks", pageInfo.getPages());
                return r;
            }
            if (p.uid == null) {
                if (p.role != null && !"shop".equals(p.role)) {
                    r.put("code", -11023);
                    r.put("message", "不匹配的身份");
                    return r;
                }
                // 不分块一次性返回所有结果
                PageInfo<User> pageInfo = PageChunk.chunk(p.block, blockSize, new Assign<>() {
                    @Override
                    public List<User> assign() {
                        return userService.findUsers(Role.shop, p.name);
                    }
                });
                r.put("code", 0);
                r.put("message", "success");
                r.put("users", pageInfo.getList());
                r.put("block", pageInfo.getPageNum());
                r.put("blocks", pageInfo.getPages());
                return r;
            }
            // uid 验证采取 string -> int
            int uid;
            User u;
            // uid 错误
            try {
                uid = Integer.parseInt(p.uid);
            } catch (Exception e) {
                r.put("code", -11011);
                r.put("message", "不正确的uid");
                return r;
            }
            // 查询自己
            if (user.uid == uid) {
                if (p.role != null && !"buyer".equals(p.role)) {
                    r.put("code", -11023);
                    r.put("message", "不匹配的身份");
                    return r;
                }
                // 直接放入引用不进行字段复制
                // 时间短不必使用锁
                r.put("code", 0);
                r.put("message", "success");
                r.put("user", user);
                return r;
            }
            // 查询商户
            if (p.role != null && !"shop".equals(p.role)) {
                r.put("code", -11013);
                r.put("message", "不匹配的身份");
                return r;
            }
            u = UserCache.getUser(uid);
            // 缓存未命中
            if (u == null) {
                u = userService.findUser(uid);
                if (u == null) {
                    r.put("code", -11011);
                    r.put("message", "不正确的uid");
                    return r;
                }
                // 添加缓存
                UserCache.put(u);
            }
            // 拒绝查询非商户用户
            if (!Role.shop.equals(u.role)) {
                r.put("code", -11023);
                r.put("message", "拒绝访问");
                return r;
            }
            if (p.name != null && !u.name.equals(p.name)) {
                r.put("code", -11053);
                r.put("message", "拒绝访问");
                return r;
            }
            // 直接放入引用不进行字段复制
            // 时间短不必使用锁
            r.put("code", 0);
            r.put("message", "success");
            r.put("user", u);
            return r;
        }
        // 管理员查询逻辑
        // 查询优先级 uid > username > (role&name) > role > 全表查询
        if (p.uid != null) {
            int uid;
            User u;
            // uid 错误
            // uid 使用采取 string -> int
            try {
                uid = Integer.parseInt(p.uid);
            } catch (Exception e) {
                r.put("code", -11011);
                r.put("message", "不正确的uid");
                return r;
            }
            u = UserCache.getUser(uid);
            // 缓存未命中
            if (u == null) {
                u = userService.findUser(uid);
                if (u == null) {
                    r.put("code", -11011);
                    r.put("message", "不正确的uid");
                    return r;
                }
                // 添加缓存
                UserCache.put(u);
            }
            if (p.username != null && !u.username.equals(p.username)) {
                r.put("code", -11032);
                r.put("message", "不正确的账户");
                return r;
            }
            if (p.name != null && !u.name.equals(p.name)) {
                r.put("code", -11052);
                r.put("message", "不正确的姓名");
                return r;
            }
            if (p.role != null && !(u.role + "").equals(p.role)) {
                r.put("code", -11022);
                r.put("message", "不正确的身份");
                return r;
            }
            // 直接放入引用不进行字段复制
            // 时间短不必使用锁
            r.put("code", 0);
            r.put("message", "success");
            r.put("user", u);
            return r;
        }
        if (p.username != null) {
            User u = userService.findUser(p.username);
            if (u == null) {
                r.put("code", -11001);
                r.put("message", "不正确的账户");
                return r;
            }
            if (p.name != null && !u.name.equals(p.name)) {
                r.put("code", -11012);
                r.put("message", "不正确的姓名");
                return r;
            }
            if (p.role != null && !(u.role + "").equals(p.role)) {
                r.put("code", -11022);
                r.put("message", "不正确的身份");
                return r;
            }
            // 直接放入引用不进行字段复制
            // 时间短不必使用锁
            r.put("code", 0);
            r.put("message", "success");
            r.put("user", u);
            return r;
        }
        if (p.name != null) {
            if (p.role == null) {
                PageInfo<User> pageInfo = PageChunk.chunk(p.block, blockSize, new Assign<>() {
                    @Override
                    public List<User> assign() {
                        return userService.findUsers(p.name);
                    }
                });
                r.put("code", 0);
                r.put("message", "success");
                r.put("users", pageInfo.getList());
                r.put("block", pageInfo.getPageNum());
                r.put("blocks", pageInfo.getPages());
                return r;
            }
            Role role;
            // 除了游客都允许查询
            switch (p.role) {
                case "admin": {
                    role = Role.admin;
                    break;
                } case "buyer": {
                    role = Role.buyer;
                    break;
                } case "shop": {
                    role = Role.shop;
                    break;
                } case "delivery": {
                    role = Role.delivery;
                    break;
                } default: {
                    r.put("code", -11022);
                    r.put("message", "不正确的身份");
                    return r;
                }
            }
            PageInfo<User> pageInfo = PageChunk.chunk(p.block, blockSize, new Assign<>() {
                @Override
                public List<User> assign() {
                    return userService.findUsers(role, p.name);
                }
            });
            r.put("code", 0);
            r.put("message", "success");
            r.put("users", pageInfo.getList());
            r.put("block", pageInfo.getPageNum());
            r.put("blocks", pageInfo.getPages());
            return r;
        }
        if (p.role != null) {
            Role role;
            // 除了游客都允许查询
            switch (p.role) {
                case "admin": {
                    role = Role.admin;
                    break;
                } case "buyer": {
                    role = Role.buyer;
                    break;
                } case "shop": {
                    role = Role.shop;
                    break;
                } case "delivery": {
                    role = Role.delivery;
                    break;
                } default: {
                    r.put("code", 1000);
                    r.put("message", "不正确的身份");
                    return r;
                }
            }
            PageInfo<User> pageInfo = PageChunk.chunk(p.block, blockSize, new Assign<>() {
                @Override
                public List<User> assign() {
                    return userService.findUsers(role);
                }
            });
            r.put("code", 0);
            r.put("message", "success");
            r.put("users", pageInfo.getList());
            r.put("block", pageInfo.getPageNum());
            r.put("blocks", pageInfo.getPages());
            return r;
        }
        // 全表查询
        PageInfo<User> pageInfo = PageChunk.chunk(p.block, blockSize, new Assign<>() {
            @Override
            public List<User> assign() {
                return userService.findUsers();
            }
        });

        r.put("code", 0);
        r.put("message", "success");
        r.put("users", pageInfo.getList());
        r.put("block", pageInfo.getPageNum());
        r.put("blocks", pageInfo.getPages());
        return r;
    }

    @PostMapping("/add")
    @ResponseBody
    public Object addUser(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = preprocessingToken(p, r);
        if (user == null) {
            return r;
        }
        // 非管理员拒绝执行
        if (!Role.admin.equals(user.role)) {
            r.put("code", -12022);
            r.put("message", "拒绝访问");
            return r;
        }
        int code;
        User u = new User();
        if (p.role == null) {
            r.put("code", -12021);
            r.put("message", "拒绝访问");
            return r;
        }
        // 除游客都允许添加
        switch (p.role) {
            case "admin": {
                u.role = Role.admin;
                break;
            } case "buyer": {
                u.role = Role.buyer;
                break;
            } case "shop": {
                u.role = Role.shop;
                break;
            } case "delivery": {
                u.role = Role.delivery;
                break;
            } default: {
                r.put("code", -12022);
                r.put("message", "错误的添加");
                return r;
            }
        }
        // 检查本地 image 文件
        if (p.image != null) {
        }
        u.username = p.username;
        u.password = p.password;
        u.name = p.name;
        u.image = p.image;
        u.tel = p.tel;
        u.address = p.address;
        u.info = p.info;
        code = userService.addUser(u);
        switch (code) {
            case 1: {
                // 获取 uid 字段
                u = userService.findUser(p.username);
                // 添加缓存
                UserCache.put(u);
                r.put("code", 0);
                r.put("message", "success");
                r.put("uid", u.uid);
                break;
            } case 0: {
                r.put("code", -50005);
                r.put("message", "错误的信息");
                break;
            } case -1: {
                r.put("code", -12021);
                r.put("message", "未选择身份");
                break;
            } case -2: {
                r.put("code", -12031);
                r.put("message", "账户信息错误");
                break;
            } case -3: {
                r.put("code", -12041);
                r.put("message", "未填写密码");
                break;
            } case -4: {
                r.put("code", -12051);
                r.put("message", "未填写姓名");
                break;
            } default: {
                r.put("code", -50005);
                r.put("message", "未知错误");
            }
        }

        return r;
    }

    @PostMapping("/upd")
    @ResponseBody
    public Object updUser(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = preprocessingToken(p, r);
        if (user == null) {
            return r;
        }
        // 游客拒绝执行
        if (Role.guest.equals(user.role)) {
            r.put("code", -13023);
            r.put("message", "拒绝访问");
            return r;
        }
        // 非管理员更新
        if (!Role.admin.equals(user.role)) {
            // 只能修改自己
            // uid 验证采取 int -> string 避免异常
            if (p.uid != null && !(user.uid + "").equals(p.uid)) {
                r.put("code", -13012);
                r.put("message", "错误的uid");
                return r;
            }
            // 拒绝切换身份
            if (p.role != null && !(user.role + "").equals(p.role)) {
                r.put("code", -13022);
                r.put("message", "错误的身份");
                return r;
            }
            // 上锁前检查本地 image 文件
            if (p.image != null) {

            }
            // 上锁->更新数据库->写入缓存并解锁
            // 锁使用
            // 伺服器繁忙检查
            if (!lockUser(user)) {
                r.put("code", -2);
                r.put("message", "系统繁忙");
                return r;
            }
            int code;
            User u = new User();
            u.uid = user.uid;
            // 相同则置 null
            if (!user.name.equals(p.username)) {
                u.username = p.username;
            }
            u.password = p.password;
            u.name = p.name;
            u.image = p.image;
            u.tel = p.tel;
            u.address = p.address;
            u.info = p.info;
            code = userService.updUser(u);
            switch (code) {
                case 1: {
                    // 获取新 user 信息
                    u = userService.findUser(user.uid);
                    // 回写缓存
                    UserCache.putUserIgnoreLock(u);
                    r.put("code", 0);
                    r.put("message", "success");
                    r.put("uid", u.uid);
                    break;
                } case 0: {
                    r.put("code", -50005);
                    r.put("message", "错误的用户");
                    break;
                } case -1: {
                    r.put("code", -13031);
                    r.put("message", "账户信息错误");
                    break;
                } case -2: {
                    r.put("code", -13031);
                    r.put("message", "账户信息错误");
                    break;
                } case -3: {
                    r.put("code", -13041);
                    r.put("message", "密码信息错误");
                    break;
                } case -4: {
                    r.put("code", -13051);
                    r.put("message", "姓名信息未知");
                    break;
                } default: {
                    r.put("code", -50005);
                    r.put("message", "未知错误");
                }
            }
            // 解锁
            unlockUser(user);
            return r;
        }
        // 取得缓存并上锁->更新数据库->写入缓存并解锁
        // 未缓存->取得用户放入缓存并上锁->更新数据库->写入缓存并解锁
        // 确保在更新时访问统一对象的其他线程阻塞
        int uid;
        User u;
        // uid 错误
        try {
            uid = Integer.parseInt(p.uid);
        } catch (Exception e) {
            r.put("code", -13011);
            r.put("message", "不正确的uid");
            return r;
        }
        u = UserCache.getUser(uid);
        // 缓存未命中
        if (u == null) {
            u = userService.findUser(uid);
            if (u == null) {
                r.put("code", -13012);
                r.put("message", "不正确的uid");
                return r;
            }
            // 添加缓存
            UserCache.put(u);
        }
        int code;
        User uu = new User();
        // 拒绝更改为游客
        if (p.role != null) {
            switch (p.role) {
                case "admin": {
                    uu.role = Role.admin;
                    break;
                } case "buyer": {
                    uu.role = Role.buyer;
                    break;
                } case "shop": {
                    uu.role = Role.shop;
                    break;
                } case "delivery": {
                    uu.role = Role.delivery;
                    break;
                } default: {
                    r.put("code", -13022);
                    r.put("message", "不正确的身份");
                    return r;
                }
            }
            // uid 为 1 的超级管理员不允许被修改 role
            if (u.uid == 1 && !Role.admin.equals(uu.role)) {
                r.put("code", -13023);
                r.put("message", "不允许修改该用户");
                return r;
            }
        }
        // 上锁前检查本地 image 文件
        if (p.image != null) {

        }
        // 锁使用
        // 伺服器繁忙检查
        if (!lockUser(u)) {
            r.put("code", -2);
            r.put("message", "系统繁忙");
            return r;
        }
        uu.uid = u.uid;
        // 相同则置 null
        if (!u.username.equals(p.username)) {
            uu.username = p.username;
        }
        uu.password = p.password;
        uu.name = p.name;
        uu.image = p.image;
        uu.tel = p.tel;
        uu.address = p.address;
        uu.info = p.info;
        code = userService.updUser(uu);
        switch (code) {
            case 1: {
                // 获取新的 user 信息
                uu = userService.findUser(u.uid);
                // 写回缓存
                UserCache.putUserIgnoreLock(uu);
                r.put("code", 0);
                r.put("message", "success");
                r.put("uid", uu.uid);
                break;
            } case 0: {
                r.put("code", -50005);
                r.put("message", "该用户不存在");
                break;
            } case -1: {
                r.put("code", -13031);
                r.put("message", "账户信息错误");
                break;
            } case -2: {
                r.put("code", -13031);
                r.put("message", "账户信息错误");
                break;
            } case -3: {
                r.put("code", -13041);
                r.put("message", "密码信息错误");
                break;
            } case -4: {
                r.put("code", -13051);
                r.put("message", "姓名信息错误");
                break;
            } default: {
                r.put("code", -50005);
                r.put("message", "未知错误");
            }
        }
        // 解锁
        unlockUser(u);

        return r;
    }

    @PostMapping("/del")
    @ResponseBody
    public Object delUser(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = preprocessingToken(p, r);
        if (user == null) {
            return r;
        }
        // 游客拒绝执行
        if (Role.guest.equals(user.role)) {
            r.put("code", -14023);
            r.put("message", "拒绝执行");
            return r;
        }
        // 非管理员只能注销自己
        if (!Role.admin.equals(user.role)) {
            // uid 验证采取 int -> string 避免异常
            if (p.uid != null && !(user.uid + "").equals(p.uid)) {
                r.put("code", -14012);
                r.put("message", "不正确的uid");
                return r;
            }
            int code = userService.delUser(user.uid);
            if (code == 0) {
                r.put("code", -50005);
                r.put("message", "不存在该用户");
                return r;
            }
            UserCache.remove(user.uid);
            r.put("code", 0);
            r.put("message", "success");
            return r;
        }
        // 取得缓存->更新数据库->清除缓存
        // 未缓存->更新数据库->清除缓存
        int uid;
        User u;
        // uid 错误
        try {
            uid = Integer.parseInt(p.uid);
        } catch (Exception e) {
            r.put("code", -14011);
            r.put("message", "不正确的uid");
            return r;
        }
        // uid 为 1 的超级管理员不能被注销
        if (uid == 1) {
            r.put("code", -14023);
            r.put("message", "拒绝执行");
            return r;
        }
        u = UserCache.getUser(uid);
        // 缓存未命中
        if (u == null) {
            u = userService.findUser(uid);
            if (u == null) {
                r.put("code", -14011);
                r.put("message", "不正确的uid");
                return r;
            }
            // 不添加缓存
        }
        // 不允许删除游客
        if (Role.guest.equals(u.role)) {
            r.put("code", -14023);
            r.put("message", "拒绝执行");
            return r;
        }
        int code = userService.delUser(u.uid);
        if (code == 0) {
            r.put("code", -14011);
            r.put("message", "不正确的uid");
            return r;
        }
        // 清除可能有的缓存
        UserCache.remove(u.uid);

        r.put("code", 0);
        r.put("message", "success");
        return r;
    }
}

class Parameter extends BaseParameter {
    public String block;
    public String uid;
    public String role;
    public String username;
    public String password;
    public String name;
    public String image;
    public String tel;
    public String address;
    public String info;
}

