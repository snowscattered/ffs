package com.ffs.controller.Order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ffs.cache.UserCache;
import com.ffs.controller.Generation.BaseParameter;
import com.ffs.controller.User.UserAPI;
import com.ffs.po.*;
import com.ffs.service.ListingService;
import com.ffs.service.OrderService;
import com.ffs.service.ProductService;
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

import java.util.*;

/**
 * OrderAPI 处理 order 和 listing 表的增删改查请求
 * 仅 POST 方法
 * @author hoshinosena
 * @version 1.0
 */
@Controller
@RequestMapping("${baseURL}" + "api/order/")
public class OrderAPI {
    @Value("${selectBlockSize}")
    int blockSize;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;
    @Autowired
    ListingService listingService;
    @Autowired
    OrderService orderService;

    private User preprocessingToken(BaseParameter p, Map<String, Object> r) {
        return UserAPI.preprocessingToken(p, r);
    }

    private User checkUser(Parameter p, Map<String, Object> r) {
        int uid;
        User u;

        // uid 错误
        try {
            uid = Integer.parseInt(p.uid);
        } catch (Exception e) {
            r.put("code", -30001);
            r.put("message", "不正确的uid");
            return null;
        }
        u = UserCache.getUser(uid);
        // 缓存未命中
        if (u == null) {
            u = userService.findUser(uid);
            if (u == null) {
                r.put("code", -30001);
                r.put("message", "不正确的uid");
                return null;
            }
            // 添加缓存
            UserCache.put(u);
        }

        return u;
    }

    private Order checkOrder(Parameter p, Map<String, Object> r) {
        int oid;
        Order o;

        // oid 错误
        try {
            oid = Integer.parseInt(p.oid);
        } catch (Exception e) {
            r.put("code", -30011);
            r.put("message", "不正确的oid");
            return null;
        }
        o = orderService.findOrder(oid);
        if (o == null) {
            r.put("code", -30011);
            r.put("message", "不正确的oid");
        }

        return o;
    }

    @PostMapping("/get")
    @ResponseBody
    public Object getOrder(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = preprocessingToken(p, r);
        if (user == null) {
            return r;
        }
        // 管理员查询逻辑
        // 查询优先级 oid > tid(不返回 listings) > uid(不返回 listings) > 全表查询(不返回 listings)
        if (Role.admin.equals(user.role)) {
            User u = null;
            if (p.uid != null) {
                u = checkUser(p, r);
                if (u == null) {
                    return r;
                }
                // 非用户商家骑手拒绝查找
                if (!Role.buyer.equals(u.role)
                    && !Role.shop.equals(u.role)
                    && !Role.delivery.equals(u.role)) {
                    r.put("code", -31002);
                    r.put("message", "拒绝执行");
                    return r;
                }
            }
            if (p.oid == p.tid) {
                User finalU = u;
                PageInfo<Order> pageInfo = PageChunk.chunk(p.block, blockSize, new Assign<>() {
                    @Override
                    public List<Order> assign() {
                        if (finalU == null) {
                            return orderService.findOrders();
                        } else {
                            if (Role.buyer.equals(finalU.role)) {
                                return orderService.findOrders_bid(finalU.uid);
                            } else if (Role.shop.equals(finalU.role)) {
                                return orderService.findOrders_sid(finalU.uid);
                            } else {
                                return orderService.findOrders_did(finalU.uid);
                            }
                        }
                    }
                });
                r.put("code", 0);
                r.put("message", "success");
                r.put("orders", pageInfo.getList());
                r.put("block", pageInfo.getPageNum());
                r.put("blocks", pageInfo.getPages());
                return r;
            }
            if (p.oid == null) {
                Order o = orderService.findOrder(p.tid);
                if (o == null) {
                    r.put("code", -31061);
                    r.put("message", "不正确的tid");
                    return r;
                }
                // 未提交 oid 时 tid 查询的 uid 自动归化为 bid
                if (u != null && u.uid != o.bid) {
                    r.put("code", -31002);
                    r.put("message", "不匹配的uid");
                    return r;
                }
                r.put("code", 0);
                r.put("message", "success");
                r.put("order", o);
                return r;
            }
            Order o = checkOrder(p, r);
            if (o == null) {
                return r;
            }
            // 提交 oid 后 tid 查询的 uid 不归化(bid&sid&did)
            // 单个订单查询返回所有订单列表
            // 不进行分块
            if (p.tid != null && !o.tid.equals(p.tid)) {
                r.put("code", -31063);
                r.put("message", "不匹配的tid");
                return r;
            }
            if (u != null
                && o.bid != u.uid
                && o.sid != u.uid
                && o.did != u.uid) {
                r.put("code", -31002);
                r.put("message", "不匹配的uid");
                return r;
            }
            // tid 和 uid 检查通过后直接查找 oid 即可
            o.listings = listingService.findListingsByOid(o.oid);
            r.put("code", 0);
            r.put("message", "success");
            r.put("order", o);
            return r;
        }
        // 用户查询逻辑
        // 只能查询自己的订单
        // 查询优先级 oid > tid(不返回 listings) > uid->bid(不返回 listings)
        if (Role.buyer.equals(user.role)) {
            // uid 验证采取 int -> string 避免异常
            if (p.uid != null && !(user.uid + "").equals(p.uid)) {
                r.put("code", -31002);
                r.put("message", "不匹配的uid");
                return r;
            }
            if (p.oid == p.tid) {
                PageInfo<Order> pageInfo = PageChunk.chunk(p.block, blockSize, new Assign<>() {
                    @Override
                    public List<Order> assign() {
                        return orderService.findOrders_bid(user.uid);
                    }
                });
                r.put("code", 0);
                r.put("message", "success");
                r.put("orders", pageInfo.getList());
                r.put("block", pageInfo.getPageNum());
                r.put("blocks", pageInfo.getPages());
                return r;
            }
            if (p.oid == null) {
                Order o = orderService.findOrder(p.tid);
                // 虽说 token 有 2^128 种可能碰撞的可能性不大, 姑且还是验证罢
                if (o == null || o.bid != user.uid) {
                    r.put("code", -3);
                    r.put("message", "系统错误");
                    return r;
                }
                r.put("code", 0);
                r.put("message", "系统错误");
                r.put("order", o);
                return r;
            }
            Order o = checkOrder(p, r);
            if (o == null) {
                return r;
            }
            // uid->bid 碰撞检查
            if (o.bid != user.uid) {
                r.put("code", -31002);
                r.put("message", "不匹配的uid");
                return r;
            }
            if (p.tid != null && !o.tid.equals(p.tid)) {
                r.put("code", -31002);
                r.put("message", "不匹配的tid");
                return r;
            }
            // 单个订单查询返回所有订单列表
            // 不进行分块
            // order 存在必然能获取到订单列表
            o.listings = listingService.findListingsByOid(o.oid);
            r.put("code", 0);
            r.put("message", "success");
            r.put("order", o);
            return r;
        }
        // 商户查询逻辑
        // 商户只允许查询自己的订单
        // 查询优先级 oid > uid->sid(不返回 listings) > 接单列表(不返回 listings)
        if (Role.shop.equals(user.role)) {
            // uid 验证采取 int -> string 避免异常
            if (p.uid != null && !(user.uid + "").equals(p.uid)) {
                r.put("code", -31002);
                r.put("message", "不匹配的uid");
                return r;
            }
            if (p.oid == null) {
                PageInfo<Order> pageInfo = PageChunk.chunk(p.block, blockSize, new Assign<>() {
                    @Override
                    public List<Order> assign() {
                        List<Order> orders=orderService.findOrders_sid(user.uid);
                        orders.removeIf(order -> order.state== State.unpaid || order.state == State.cancel);
                        return orders;
                    }
                });
                r.put("code", 0);
                r.put("message", "success");
                r.put("orders", pageInfo.getList());
                r.put("block", pageInfo.getPageNum());
                r.put("blocks", pageInfo.getPages());
                return r;
            }
            Order o = checkOrder(p, r);
            if (o == null) {
                return r;
            }
            // uid->sid/did 碰撞检查
            if (o.sid != user.uid) {
                r.put("code", -31002);
                r.put("message", "不匹配的uid");
                return r;
            }
            // 单个订单查询返回所有订单列表
            // 不进行分块
            // order 存在必然能获取到订单列表
            o.listings = listingService.findListingsByOid(o.oid);
            r.put("code", 0);
            r.put("message", "success");
            r.put("order", o);
            return r;
        }
        // 骑手查询逻辑
        // 骑手允许查询接单列表
        // 查询优先级 oid(不返回 listings) > uid->did(不返回 listings) > 接单列表(不返回 listings)
        if (Role.delivery.equals(user.role)) {
            // 不提交 oid 与 uid 返回待接单列表
            if (p.oid == p.uid) {
                PageInfo<Order> pageInfo = PageChunk.chunk(p.block, blockSize, new Assign<>() {
                    @Override
                    public List<Order> assign() {
                        return orderService.findOrdersByState(State.received);
                    }
                });
                r.put("code", 0);
                r.put("message", "success");
                r.put("orders", pageInfo.getList());
                r.put("block", pageInfo.getPageNum());
                r.put("blocks", pageInfo.getPages());
                return r;
            }
            // uid 验证采取 int -> string 避免异常
            if (p.uid != null && !(user.uid + "").equals(p.uid)) {
                r.put("code", -31002);
                r.put("message", "不匹配的uid");
                return r;
            }
            if (p.oid == null) {
                PageInfo<Order> pageInfo = PageChunk.chunk(p.block, blockSize, new Assign<>() {
                    @Override
                    public List<Order> assign() {
                        return orderService.findOrders_did(user.uid);
                    }
                });
                r.put("code", 0);
                r.put("message", "success");
                r.put("orders", pageInfo.getList());
                r.put("block", pageInfo.getPageNum());
                r.put("blocks", pageInfo.getPages());
                return r;
            }
            Order o = checkOrder(p, r);
            if (o == null) {
                return r;
            }
            if(o.did == null) {
                r.put("code", 0);
                r.put("message", "success");
                Order order=new Order();
                order.oid = o.oid;
                order.sa = o.sa;
                order.ba = o.ba;
                order.state = o.state;
                r.put("order", order);
                return r;
            }
            // uid->sid/did 碰撞检查
            if (o.did != user.uid) {
                r.put("code", -31002);
                r.put("message", "不匹配的uid");
                return r;
            }
            // 骑手查询不返回 listing
            r.put("code", 0);
            r.put("message", "success");
            r.put("order", o);
            return r;
        }

        // 拒绝查询
        r.put("code", -31003);
        r.put("message", "拒绝执行");
        return r;
    }

    @PostMapping("/add")
    @ResponseBody
    public Object addOrder(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = preprocessingToken(p, r);
        if (user == null) {
            return r;
        }
        // 只允许用户添加订单
        if (!Role.buyer.equals(user.role)){
            r.put("code", -32003);
            r.put("message", "不正确的身份");
            return r;
        }
        // listing 不存在
        if (p.listings == null) {
            r.put("code", -31991);
            r.put("message", "订单不存在");
            return r;
        }
        User u = checkUser(p, r);
        // 商户不存在
        if (u == null || !Role.shop.equals(u.role)) {
            r.put("code", -31041);
            r.put("message", "商家不存在");
            return r;
        }
        int code;
        // 透过 mark 标记 tid 实现查找
        String mark = UUID.randomUUID().toString();
        Order o = new Order();
        // 拷贝订单信息
        o.bid = user.uid;
        o.sid = u.uid;
        o.tid = mark;
        o.info = p.info;
        o.bn = user.name;
        o.bt = user.tel;
        o.ba = user.address;
        o.sn = u.name;
        o.st = u.tel;
        o.sa = u.address;
        code = orderService.addOrder(o);
        if (code == 0) {
            r.put("code", -50005);
            r.put("message", "未知错误");
            return r;
        }
        Listing[] listings;
        // 反序列化 listings
        try {
            listings = objectMapper.readValue(p.listings, Listing[].class);
        } catch (Exception e) {
            r.put("code", -32991);
            r.put("message", "错误的订单信息");
            return r;
        }
        // 检查 listing 的 pid 与 uid 关系
        // 反其道而行之, 先查询 product 再检查 listing
        List<Product> pds = productService.findProducts(u.uid);
        code = listings.length;
        // 数量超出必然 listings 错误
        // 确保 pds 不会越界
        if (pds.size() < code) {
            r.put("code", -32991);
            r.put("message", "错误的订单");
            return r;
        }
        // 刷新 order 获取 oid
        o = orderService.findOrder(mark);
        // pds 本身有序(自然增长)
        // 二元搜寻法使用
        for (int i=0; i<code; i++) {
            int min = 0,
                max = pds.size() - 1,
                middle;
            int pid;
            // listing 不得为 null
            try {
                pid = listings[i].pid;
            } catch (Exception e) {
                r.put("code", -42031);
                r.put("message", "订单信息错误");
                return r;
            }
            int ppid;
            boolean flag = true;
            while (true) {
                // 不考虑溢出
                middle = (min + max) >> 1;
                ppid = pds.get(middle).pid;
                if (middle == max) {
                    if (pid == ppid) {
                        flag = false;
                    }
                    break;
                } else if (ppid > pid) {
                    min = middle + 1;
                } else {
                    max = middle;
                }
            }
            // 找不到商品
            if (flag) {
                // 回滚 order
                orderService.delOrder(o.oid);
                r.put("code", -42041);
                r.put("message", "不存在的商品");
                return r;
            }
            // 0 < amount
            if (listings[i].amount <= 0) {
                // 回滚 order
                orderService.delOrder(o.oid);
                r.put("code", -42051);
                r.put("message", "不正确的数量");
                return r;
            }
            // 赋值 uid 和 oid
            listings[i].uid = user.uid;
            listings[i].oid = o.oid;
        }
        code = listingService.addListings(Arrays.asList(listings));
        if (code == 0) {
            // 回滚 order
            orderService.delOrder(o.oid);
            r.put("code", -31991);
            r.put("message", "不正确的订单");
            return r;
        }
        o.state = null;
        o.tid = "";
        o.info = null;
        // 抹去 mark
        // 理论上不会出错
        code = orderService.updOrder(o);
//        if (code == 0) {
//            r.put("code", -180);
//            r.put("message", "test");
//            return r;
//        }
        r.put("code", 0);
        r.put("message", "success");
        r.put("oid", o.oid);
        return r;
    }

    @PostMapping("/upd")
    @ResponseBody
    public Object updOrder(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = preprocessingToken(p, r);
        if (user == null) {
            return r;
        }
        // 用户更新逻辑
        // unpaid->paid, unpaid/paid->cancel, tid->tid
        // info->info
        if (Role.buyer.equals(user.role)) {
            Order o = checkOrder(p, r);
            if (o == null) {
                return r;
            }
            if (o.bid != user.uid) {
                r.put("code", -33032);
                r.put("message", "不匹配的uid");
                return r;
            }
            // 保留 state
            State state = o.state;
            // order 初期化
            // 若未提交字段, 可以透过更新失败判明
            o.state = null;
            o.did = null;
            o.tid = null;
            o.info = p.info;
            o.dn = null;
            o.dt = null;
            if (p.state != null) {
                switch (p.state) {
                    case "paid": {
                        if (!State.unpaid.equals(state)) {
                            r.put("code", -33022);
                            r.put("message", "不匹配的状态");
                            return r;
                        }
                        // 支付逻辑
                        if (true) {
                            o.state = State.paid;
                            o.tid = UUID.randomUUID().toString();
                        }
                        break;
                    } case "cancel": {
                        if (!State.unpaid.equals(state)
                            && !State.paid.equals(state)) {
                            r.put("code", -33021);
                            r.put("message", "删除失败");
                            return r;
                        }
                        o.state = State.cancel;
                        break;
                    } default: {
                        r.put("code", -33021);
                        r.put("message", "错误的选择");
                        return r;
                    }
                }
            }
            int code = orderService.updOrder(o);
            if (code == 0) {
                r.put("code", -50005);
                r.put("message", "不存在该订单");
                return r;
            }

            r.put("code", 0);
            r.put("message", "success");
            r.put("oid", o.oid);
            return r;
        }
        // 商户更新逻辑
        // paid->received
        if (Role.shop.equals(user.role)) {
            Order o = checkOrder(p, r);
            if (o == null) {
                return r;
            }
            if (o.sid != user.uid) {
                r.put("code", -33042);
                r.put("message", "不匹配的uid");
                return r;
            }
            if (!State.paid.equals(o.state)) {
                r.put("code", -33022);
                r.put("message", "错误的选择");
                return r;
            }
            if (!"received".equals(p.state)) {
                r.put("code", -33022);
                r.put("message", "错误的选择");
                return r;
            }
            o.state = State.received;
            o.did = null;
            o.tid = null;
            o.info = null;
            o.dn = null;
            o.dt = null;
            int code = orderService.updOrder(o);
            if (code == 0) {
                r.put("code", -50005);
                r.put("message", "不存在该订单");
                return r;
            }
            r.put("code", 0);
            r.put("message", "success");
            r.put("oid", o.oid);
            return r;
        }
        // 骑手更新逻辑
        // received->delivering, delivering->finish, did->did, dn->dn, dt->dt
        if (Role.delivery.equals(user.role)) {
            Order o = checkOrder(p, r);
            if (o == null) {
                return r;
            }
            switch (p.state) {
                case "delivering": {
                    if (!State.received.equals(o.state)) {
                        r.put("code", -33022);
                        r.put("message", "错误的选择");
                        return r;
                    }
                    o.state = State.delivering;
                    o.did = user.uid;
                    o.tid = null;
                    o.info = null;
                    o.dn = user.name;
                    o.dt = user.tel;
                    break;
                } case "finish": {
                    if (o.did != user.uid) {
                        r.put("code", -33022);
                        r.put("message", "错误的选择");
                        return r;
                    }
                    if (!State.delivering.equals(o.state)) {
                        r.put("code", -33022);
                        r.put("message", "错误的选择");
                        return r;
                    }
                    o.state = State.finish;
                    o.did = null;
                    o.tid = null;
                    o.info = null;
                    o.dn = null;
                    o.dt = null;
                    break;
                } default: {
                    r.put("code", -50005);
                    r.put("message", "未知错误");
                    return r;
                }
            }
            int code = orderService.updOrder(o);
            if (code == 0) {
                r.put("code", -50005);
                r.put("message", "不存在该订单");
                return r;
            }
            r.put("code", 0);
            r.put("message", "success");
            r.put("oid", o.oid);
            return r;
        }

        // 拒绝更新
        r.put("code", -33023);
        r.put("message", "拒绝执行");
        return r;
    }

    @PostMapping("/del")
    @ResponseBody
    public Object delOrder(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = preprocessingToken(p, r);
        if (user == null) {
            return r;
        }
        // 只允许用户删除订单
        if (!Role.buyer.equals(user.role)){
            r.put("code", -34002);
            r.put("message", "拒绝执行");
            return r;
        }
        Order o = checkOrder(p, r);
        if (o == null) {
            return r;
        }
        if (o.bid != user.uid) {
            r.put("code", -34052);
            r.put("message", "不匹配的uid");
            return r;
        }
        // 只允许删除 finish 和 cancel 订单
        if (!State.finish.equals(o.state)
            && !State.cancel.equals(o.state)) {
            r.put("code", -34022);
            r.put("message", "不正确的选择");
            return r;
        }
        int code = orderService.delOrder(o.oid);
        if (code == 0) {
            r.put("code", -50005);
            r.put("message", "不存在该订单");
            return r;
        }

        r.put("code", 0);
        r.put("message", "success");
        return r;
    }
}

class Parameter extends BaseParameter {
    public String block;
    public String state;
    public String uid;
    public String oid;
    public String tid;
    public String info;
    public String listings;
}
