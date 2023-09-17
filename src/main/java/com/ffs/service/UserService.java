package com.ffs.service;

import com.ffs.po.Role;
import com.ffs.po.User;

import java.util.List;

/**
 * UserService 封装了代理类 UserMapper 并
 * 在执行相应方法前进行数据检查确保返回值是预期
 * 的
 * @author hoshinosena
 * @version 1.0
 */
public interface UserService {
    /**
     * 透过 uid 查找 user
     * @param uid user 的 uid 字段
     * @return null 或 user 引用
     * @author hoshinosena
     */
    User findUser(int uid);

    /**
     * 透过 username 查找 user
     * @param username user 的 username 字段
     * @return null 或 user 引用
     * @author hoshinosena
     */
    User findUser(String username);

    /**
     * 查找所有与枚举变量 role 相同的 user
     * @param role user 的枚举常量 role
     * @return 非 null 的 List<User> 引用
     * @author hoshinosena
     */
    List<User> findUsers(Role role);

    /**
     * 透过 name 模糊查找 user
     * @param name user 的 name 字段
     * @return 非 null 的 List<User> 引用
     * @author hoshinosena
     */
    List<User> findUsers(String name);

    /**
     * 获取所有 user
     * @return 非 null 的 List<User> 引用
     * @author hoshinosena
     */
    List<User> findUsers();

    /**
     * 添加 user
     * @param user 除了引用为 null 外能够
     *             检查所有数据错误, 可以透
     *             过返回值了解错误原因
     * @return  1：添加成功
     *          0：来自数据库的错误, 也可能是
     *          username 已被占用, 也可能是
     *          user 不存在
     *         -1: role 为 null
     *         -2: username 为 null 或空
     *         -3: password 为 null 或空
     *         -4: name 为 null 或空
     * @author hoshinosena
     */
    int addUser(User user);

    /**
     * 更新 user
     * @param user null 字段对应的数据不会被
     *             更新, 除了引用为 null 外
     *             能够检查所有数据错误, 可
     *             以透过返回值了解错误原因
     * @return  1：更新成功
     *          0：user 不存在或来自数据库
     *          的错误
     *         -2: username 为 空
     *         -3: password 为 空
     *         -4: name 为 空
     * @author hoshinosena
     */
    int updUser(User user);

    /**
     * 删除 user
     * @param uid user 的 uid 字段
     * @return 1: 删除成功
     *         0: uid 不存在或来自数据库
     *         的错误
     * @author hoshinosena
     */
    int delUser(int uid);
}
