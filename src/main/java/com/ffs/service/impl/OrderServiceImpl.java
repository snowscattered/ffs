package com.ffs.service.impl;

import com.ffs.mapper.OrderMapper;
import com.ffs.po.Order;
import com.ffs.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * OrderServiceImpl 实现了 OrderService
 * 详细信息在 OrderService 接口中说明
 * @author hoshinosena
 * @version 1.0
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;

    @Override
    public Order findOrder(int oid) {
       return orderMapper.selectOrderByOid(oid);
    }

    @Override
    public Order findOrder(String tid) {
        if ("".equals(tid)) {
            return null;
        }

        return orderMapper.selectOrderByTid(tid);
    }

    @Override
    public List<Order> findOrders_bid(int bid) {
        return orderMapper.selectOrderByBid(bid);
    }

    @Override
    public List<Order> findOrders_sid(int sid) {
        return orderMapper.selectOrderBySid(sid);
    }

    @Override
    public List<Order> findOrders_did(int did) {
        return orderMapper.selectOrderByDid(did);
    }

    @Override
    public List<Order> findOrders() {
        return orderMapper.selectOrders();
    }

    @Override
    public int addOrder(Order order) {
        order.date = new Timestamp(System.currentTimeMillis()).toString();
        if (order.info == null) {
            order.info = "";
        }

        try {
            return orderMapper.insertOrder(order);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int updOrder(Order order) {
        int state = order.state == null ? -1 : order.state.ordinal();

        try {
            return orderMapper.updateOrder(order, state);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int delOrder(int oid) {
        return orderMapper.deleteOrder(oid);
    }
}
