package com.ffs.controller.User;

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

class Pare
{
    public String uid;
    public String name;
    public User onw;
    public User other;
    public String GR;
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
     * @param pare 为页面传进的 json 对应值
     * @return 返回对应信息和信息体
     * @author snowscattered
     */
    @RequestMapping("/get")
    @ResponseBody
    public Object getUser(@RequestBody Pare pare)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        String uid = pare.uid == null ? "" : pare.uid;
        String name = pare.name == null ? "" : pare.name;
        String GR = pare.GR == null ? "" : pare.GR;
        User own = pare.onw;

        if (own == null)
        {
            objs.put("code", "");
            objs.put("message", "非法操作");
            return objs;
        }

        if (own.role == Role.admin)
        {
            if (name.equals("") && uid.equals(""))
            {
                objs.put("users", userService.findUsers());
                objs.put("code", "");
                objs.put("message", "success");
            } else if (!name.equals(""))
            {
                objs.put("users", userService.findUsers(name));
                objs.put("code", "");
                objs.put("message", "success");
            } else
            {
                int checkUid;
                try
                {
                    checkUid = Integer.parseInt(uid);
                } catch (Exception e)
                {
                    objs.put("code", "");
                    objs.put("message", "不正确的uid");
                    return objs;
                }
                objs.put("user", userService.findUser(checkUid));
                objs.put("code", "");
                objs.put("message", "success");
            }
        }
        if (own.role == Role.shop || own.role == Role.delivery)
        {
            objs.put("user", own);
            objs.put("code", "");
            objs.put("message", "success");
        }
        if (own.role == Role.buyer)
        {
            if (GR.equals("mine"))
            {
                objs.put("user", own);
                objs.put("code", "");
                objs.put("message", "success");
            } else
            {
                List<User> users = userService.findUsers(name);
                users.removeIf(user -> user.role != Role.shop);
                objs.put("users", users);
                objs.put("code", "");
                objs.put("message", "success");
            }
        }
        return objs;
    }

    /**
     * 添加 user
     * 当 role 为 admin 时，可以创建一切合法账户
     * 当 role 不为 admin 时，可以创建除 admin 以外的账户(保留意见)
     * @param pare
     * @return 返回显示json
     * @author snowscattered
     */
    @RequestMapping("/add")
    @ResponseBody
    public Object addUser(@RequestBody Pare pare)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        User own = pare.onw;
        User other = pare.other;

        if (own == null || other == null)
        {
            objs.put("code", "");
            objs.put("message", "非法操作");
            return objs;
        }

        if (own.role != Role.admin)
        {
            if (other.role == Role.admin)
            {
                objs.put("code", "");
                objs.put("message", "非法操作");
                return objs;
            }
        }
        //该函数有多个返回值
        int status = userService.addUser(other);
        if (status == 1)
        {
            objs.put("code", "");
            objs.put("message", "success");
        } else
        {
            objs.put("code", "");
            objs.put("message", "user异常");
        }
        return objs;
    }

    /**
     * 添加 user
     * 当 role 为 admin 时，可以修改一切合法账户(自身除外)
     * 当 role 不为 admin 时，只能修改个人账户(保留意见)
     * @param pare
     * @return 返回显示 json
     * @author snowscattered
     */
    @RequestMapping("/updata")
    @ResponseBody
    public Object updataUser(@RequestBody Pare pare)
    {

        Map<String, Object> objs = new LinkedHashMap<>();
        User own = pare.onw;
        User other = pare.other;

        if (own == null || other == null)
        {
            objs.put("code", "");
            objs.put("message", "非法操作");
            return objs;
        }

        //待修改
        if (own.role == Role.admin)
        {
            if(own.uid==other.uid)
            {
                objs.put("code","");
                objs.put("message","操作异常");
                return objs;
            } else if (userService.updUser(other) == 1)
            {
                objs.put("code", "");
                objs.put("message", "success");
            } else
            {
                objs.put("code", "");
                objs.put("message", "user异常");
            }
        } else
        {
            if(own.uid!=other.uid)
            {
                objs.put("code", "");
                objs.put("message", "操作异常");
                return objs;
            }
            //该函数有多个返回值
            int status = userService.updUser(other);
            if (status == 1)
            {
                objs.put("code", "");
                objs.put("message", "success");
            } else
            {
                objs.put("code", "");
                objs.put("message", "user异常");
            }
        }
        return objs;
    }

    /**
     * 添加 user
     * 当 role 为 admin 时，可以删除一切合法账户(自身除外)
     * 当 role 不为 admin 时，只能删除(即注销)个人账户(保留意见)
     * @param pare
     * @return 返回显示 json
     * @author snowscattered
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object daleteUser(@RequestBody Pare pare)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        User own = pare.onw;
        String uid = pare.uid == null ? "" : pare.uid;

        if (own == null)
        {
            objs.put("code", "");
            objs.put("message", "非法操作");
            return objs;
        }

        //checkuid必传
        int checkUid;
        try
        {
            checkUid = Integer.parseInt(uid);
        } catch (Exception e)
        {
            objs.put("code", "");
            objs.put("message", "不正确的uid");
            return objs;
        }

        if (own.uid == checkUid && own.role == Role.admin)
        {
            objs.put("code", "");
            objs.put("message", "操作异常");
        } else if (userService.delUser(checkUid) == 0)
        {
            objs.put("code", "");
            objs.put("message", "不正确的uid");
        } else
        {
            objs.put("code", "");
            objs.put("message", "success");
        }
        return objs;
    }
}