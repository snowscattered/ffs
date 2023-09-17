package com.ffs.service;

import com.ffs.po.Order;
import com.ffs.po.Role;
import com.ffs.po.State;
import com.ffs.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * OrderService 的测试方法
 * @author hoshinosena
 * @version 1.0
 */
@SpringBootTest
public class OrderServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    OrderService orderService;

    @Test
    public void test() throws Exception {
        List<User> users = userService.findUsers();
        for (int i=users.size()-1; 0<=i; i--) {
            userService.delUser(users.get(i).uid);
        }
        List<Order> orders = orderService.findOrders();
        for (int i=orders.size()-1; 0<=i; i--) {
            orderService.delOrder(orders.get(i).oid);
        }

        addOrder();
        updOrder();
        findOrder();
        delOrder();
    }

    public void addOrder() throws Exception {
        Order order = new Order();

        order.bid = -1;
        if (orderService.addOrder(order) != 0) {
            throw new Exception();
        }
        User user = new User();
        user.username = "test";
        user.password = "test";
        user.name = "test";
        user.role = Role.admin;
        userService.addUser(user);
        user = userService.findUsers().get(0);
        order.bid = user.uid;
        if (orderService.addOrder(order) != 1) {
            throw new Exception();
        }
        order = orderService.findOrders().get(0);
        if (order.bid != user.uid) {
            throw new Exception();
        } else if (!"".equals(order.info)) {
            throw new Exception();
        }
        order.info = "test";
        if (orderService.addOrder(order) != 1) {
            throw new Exception();
        }
        List<Order> orders = orderService.findOrders();
        order = orders.get(0).oid == order.oid ? orders.get(1) : orders.get(0);
        if (order.bid != user.uid) {
            throw new Exception();
        } else if (!"test".equals(order.info)) {
            throw new Exception();
        }
    }

    public void updOrder() throws Exception {
        Order order = orderService.findOrders().get(0);
        Order order1 = orderService.findOrders().get(0);
        User user = new User();
        user.username = "test1";
        user.password = "test1";
        user.name = "test1";
        user.role = Role.shop;
        userService.addUser(user);
        user = userService.findUser("test1");
        order1.bid = user.uid;
        order1.date = new Timestamp(System.currentTimeMillis()).toString();
        if (orderService.updOrder(order1) != 1) {
            System.out.println(orderService.updOrder(order1));
            throw new Exception();
        }
        order1 = orderService.findOrder(order1.oid);
        if (!Objects.equals(order.bid, order1.bid)) {
            throw new Exception();
        } else if (!Objects.equals(order.date, order1.date)) {
            throw new Exception();
        }
        User user1;
        user.username = "test2";
        userService.addUser(user);
        user1 = userService.findUser("test2");
        order1.state = State.delivering;
        order1.sid = user1.uid;
        order1.did = user1.uid;
        order1.tid = "testtest";
        order1.info = "testtest";
        if (orderService.updOrder(order1) != 1) {
            throw new Exception();
        }
        order1 = orderService.findOrder(order1.oid);
        if (!order1.state.equals(State.delivering)) {
            throw new Exception();
        } else if (order1.sid != user1.uid) {
            throw new Exception();
        } else if (order1.did != user1.uid) {
            throw new Exception();
        } else if (!"testtest".equals(order1.tid)) {
            throw new Exception();
        } else if (!"testtest".equals(order1.info)) {
            throw new Exception();
        }
    }

    public void findOrder() throws Exception {
        User user = new User();
        user.username = "test3";
        user.password = "test3";
        user.name = "test3";
        user.role = Role.guest;
        userService.addUser(user);
        user = userService.findUser("test3");
        Order order = new Order();
        order.bid = user.uid;
        orderService.addOrder(order);
        List<Order> orders = orderService.findOrders();
        for (int i=orders.size()-1; 0<=i; i--) {
            if (orders.get(i).bid == user.uid) {
                order = orders.get(i);
                break;
            }
        }
        order.sid = user.uid;
        order.did = user.uid;
        orderService.updOrder(order);
        orders = orderService.findOrders_bid(user.uid);
        if (orders.size() != 1) {
            throw new Exception();
        }
        orders = orderService.findOrders_bid(user.uid);
        if (orders.size() != 1) {
            throw new Exception();
        }
        orders = orderService.findOrders_did(user.uid);
        if (orders.size() != 1) {
            System.out.println(orders.size());
            throw new Exception();
        }
    }

    public void delOrder() throws Exception {
        List<Order> orders = orderService.findOrders();
        for (int i=orders.size()-1; 0<=i; i--) {
            orderService.delOrder(orders.get(i).oid);
        }
        orders = orderService.findOrders();
        if (orders.size() != 0) {
            throw new Exception();
        }
    }
}
