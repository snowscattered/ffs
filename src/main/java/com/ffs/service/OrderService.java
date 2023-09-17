package com.ffs.service;

import com.ffs.po.Order;

import java.util.List;

/**
 * OrderService 封装了代理类 OrderMapper 并在执行
 * 相应方法前进行数据检查确保返回值是预期的
 * @author hoshinosena
 * @version 1.0
 */
public interface OrderService {
    /**
     * 透过 oid 查找 order
     * @param oid order 的 oid 字段
     * @return null 或 order 引用
     * @author hoshinosena
     */
    Order findOrder(int oid);

    /**
     * 透过 tid(付款凭证) 查找 order
     * @param tid order 的 tid 字段
     * @return null 或 order 引用
     * @author hoshinosena
     */
    Order findOrder(String tid);

    /**
     * 透过 bid(下注用户) 查找 order
     * @param bid order 的 bid 字段
     * @return 非 null 的 List<Order> 引用
     * @author hoshinosena
     */
    List<Order> findOrders_bid(int bid);

    /**
     * 透过 sid(受注商家) 查找 order
     * @param sid order 的 sid 字段
     * @return 非 null 的 List<Order> 引用
     * @author hoshinosena
     */
    List<Order> findOrders_sid(int sid);

    /**
     * 透过 did(配送骑手) 查找 order
     * @param did order 的 did 字段
     * @return 非 null 的 List<Order> 引用
     * @author hoshinosena
     */
    List<Order> findOrders_did(int did);

    /**
     * 获取所有 order
     * @return 非 null 的 List<Order> 引用
     * @author hoshinosena
     */
    List<Order> findOrders();

    /**
     * 添加 order
     * @param order 除了引用为 null 外能够
     *              检查所有数据错误
     * @return 1: 添加成功
     *         0: 来自数据库的错误
     * @author hoshinosena
     */
    int addOrder(Order order);

    /**
     * 更新 order
     * @param order 除了引用为 null 外能够
     *              检查所有数据错误
     * @return 1: 更新成功
     *         0: order 不存在或来自数据库
     *         的错误
     * @author hoshinosena
     */
    int updOrder(Order order);

    /**
     * 删除 order
     * @param oid order 的 oid 字段
     * @return 1: 更新成功
     *         0: oid 不存在来自数据库
     *         的错误
     * @author hoshinosena
     */
    int delOrder(int oid);
}
