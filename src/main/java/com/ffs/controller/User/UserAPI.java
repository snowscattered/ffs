package com.ffs.controller.User;

import com.ffs.cache.Info;
import com.ffs.cache.TokenPool;
import com.ffs.cache.UserCache;
import com.ffs.po.Role;
import com.ffs.po.User;
import com.ffs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class Para
{
    public String uid;
    public String name;
    public String token;
    public User other;
    public String role;
}
/**
 * 针对用户时的操作处理
 * @author snowscattered
 */
@Controller
@RequestMapping("${baseURL}"+"api/user")
public class UserAPI
{
    @Autowired
    UserService userService;

    /**
     * 获取 user
     * 当 Role 为 buyer 时,分别用于获取自己和所有商家或者指定商家的 user,并分页
     * 当 Role 为 shop 和 delivery 时,用于获取自身的个人信息
     * 当 Role 为 admin 时,用于获取所有的用户或者指定 user,并分页
     * @param para 为页面传进的 json 对应值
     * @return 返回对应信息和信息体
     * @author snowscattered
     */
    @RequestMapping("/get")
    @ResponseBody
    public Object getUser(@RequestBody Para para)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        String uid = para.uid == null ? "" : para.uid;
        String name = para.name == null ? "" : para.name;
        String role = para.role == null ? "" : para.role;
        String token= para.token==null?"": para.token;
        User own = UserCache.getUser(token);
        if (own == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作1");
            return objs;
        }

        if (own.role == Role.admin)
        {
            List<User> users;
            if(name.equals("")){
                users=userService.findUsers();
            } else {
                users = userService.findUsers(name);
            }

            if(!role.equals(""))
            {
                users.removeIf(user -> user.role!=Role.valueOf(role));
            }
            if (uid.equals(""))
            {
                objs.put("users", users);
                objs.put("code", 0);
                objs.put("message", "success");
            } else
            {
                int checkUid;
                User user;
                try
                {
                    checkUid = Integer.parseInt(uid);
                } catch (Exception e)
                {
                    objs.put("code", 10003);
                    objs.put("message", "不正确的uid");
                    return objs;
                }
                user = UserCache.getUser(checkUid);
                if (user == null) {
                    user = userService.findUser(checkUid);
                    UserCache.put(user);
                }
                objs.put("user", user);
                objs.put("code", 0);
                objs.put("message", "success");
            }
        }
        if (own.role == Role.shop || own.role == Role.delivery)
        {
            objs.put("user", own);
            objs.put("code", 0);
            objs.put("message", "success");
        }
        if (own.role == Role.buyer)
        {
            if (role.equals("buyer"))
            {
                objs.put("user", own);
                objs.put("code", 0);
                objs.put("message", "success");
            } else if(!name.equals(""))
            {
                List<User> users = userService.findUsers(name);
                users.removeIf(user -> user.role != Role.shop);
                objs.put("users", users);
                objs.put("code", 0);
                objs.put("message", "success");
            } else
            {
                List<User> users = userService.findUsers();
                users.removeIf(user -> user.role != Role.shop);
                objs.put("users", users);
                objs.put("code", 0);
                objs.put("message", "success");
            }
        }
        return objs;
    }

    /**
     * 添加 user
     * 当 role 为 admin 时，可以创建一切合法账户
     * 当 role 不为 admin 时，可以创建除 admin 以外的账户(保留意见)
     * @param para
     * @return 返回显示json
     * @author snowscattered
     */
    @RequestMapping("/add")
    @ResponseBody
    public Object addUser(@RequestBody Para para)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        User other = para.other;
        String token=para.token==null?"":para.token;
        User own = UserCache.getUser(token);
        if (own == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作1");
            return objs;
        }

        if (own.role != Role.admin)
        {
            if (other.role == Role.admin)
            {
                objs.put("code", 10001);
                objs.put("message", "非法操作2");
                return objs;
            }
        }
        //该函数有多个返回值
        int status = userService.addUser(other);
        if (status == 1)
        {
            objs.put("code", 0);
            objs.put("message", "success");
        } else
        {
            objs.put("code", 10002);
            objs.put("message", "user异常");
        }
        return objs;
    }

    /**
     * 添加 user
     * 当 role 为 admin 时，可以修改一切合法账户
     * 当 role 不为 admin 时，只能修改个人账户(保留意见)
     * @param para
     * @return 返回显示 json
     * @author snowscattered
     */
    @RequestMapping("/update")
    @ResponseBody
    public Object updateUser(@RequestBody Para para)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        User other = para.other;
        String token=para.token==null?"":para.token;
        User own = UserCache.getUser(token);
        if (own == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作1");
            return objs;
        }
        //待修改
        if (own.role == Role.admin)
        {
            if (userService.updUser(other) == 1)
            {
                objs.put("code", 0);
                objs.put("message", "success");
            } else
            {
                objs.put("code", 10002);
                objs.put("message", "user异常");
            }
        } else
        {
            if(own.uid!=other.uid)
            {
                objs.put("code", 10005);
                objs.put("message", "操作异常");
                return objs;
            }
            //该函数有多个返回值
            int status = userService.updUser(other);
            if (status == 1)
            {
                objs.put("code", 0);
                objs.put("message", "success");
            } else
            {
                objs.put("code", 10002);
                objs.put("message", "user异常");
            }
        }
        return objs;
    }

    /**
     * 添加 user
     * 当 role 为 admin 时，可以删除一切合法账户(自身除外)
     * 当 role 不为 admin 时，只能删除(即注销)个人账户(保留意见)
     * @param para
     * @return 返回显示 json
     * @author snowscattered
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object daleteUser(@RequestBody Para para)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        String uid = para.uid == null ? "" : para.uid;
        String token= para.token==null?"": para.token;
        User own = UserCache.getUser(token);
        if (own == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作1");
            return objs;
        }

        //checkuid必传
        int checkUid;
        try
        {
            checkUid = Integer.parseInt(uid);
        } catch (Exception e)
        {
            objs.put("code", 10003);
            objs.put("message", "不正确的uid");
            return objs;
        }

        if (own.uid == checkUid && own.role == Role.admin)
        {
            objs.put("code", 10005);
            objs.put("message", "操作异常");
        } else if (userService.delUser(checkUid) == 0)
        {
            objs.put("code", 10003);
            objs.put("message", "不正确的uid");
        } else
        {
            objs.put("code", 0);
            objs.put("message", "success");
        }
        return objs;
    }
}