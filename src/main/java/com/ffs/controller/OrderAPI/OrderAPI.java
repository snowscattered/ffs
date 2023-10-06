package com.ffs.controller.OrderAPI;

import com.ffs.cache.Info;
import com.ffs.cache.TokenPool;
import com.ffs.po.*;
import com.ffs.service.ListingService;
import com.ffs.service.OrderService;
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

    public String token;
    public String oid;
    public String state;
    public Order order;
    public List<Listing> listings;
}

/**
 * 针对订单的操作处理
 * @author snowscattered
 */
@Controller
@RequestMapping("${baseURL}"+"api/order")
public class OrderAPI
{
    @Autowired
    OrderService orderService;
    @Autowired
    ListingService listingService;
    @Autowired
    TokenPool tokenPool;

    /**
     * 查找 order
     * 当 role 为 admin 时,可以查看所有或者指定的订单
     * 当 role 为 shop 时,仅查看自身的所有订单
     * 当 role 为 delivery 时,可以查看所有自身的订单或者被商家接单的订单
     * 当 role 为 buyer 时,可以查看自己所有或者指定的订单
     * @param para 为页面传进的 json 对应值
     * @return 返回对应信息和信息体
     * @author snowscattered
     */
    @RequestMapping("/get")
    @ResponseBody
    public Object getorder(@RequestBody Para para)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        String oid = para.oid==null?"":para.oid;
        String token= para.token==null?"": para.token;
        Info info=tokenPool.pool.get(token);
        if (info == null)
        {
            objs.put("code", 10021);
            objs.put("message", "非法操作");
            return objs;
        }
        User own= info.user;

        int checkoid = 0;
        if (!oid.equals(""))
        {
            try
            {
                checkoid = Integer.parseInt(oid);
            } catch (Exception e)
            {
                objs.put("code", 10022);
                objs.put("message", "不正确的sid");
                return objs;
            }
        }

        if (own.role == Role.admin)
        {
            if (oid.equals(""))
            {
                objs.put("orders", orderService.findOrders());
                objs.put("code", 0);
                objs.put("message", "success");
            } else
            {
                objs.put("order", orderService.findOrder(checkoid));
                objs.put("code", 0);
                objs.put("message", "success");
            }
        } else if (own.role == Role.shop)
        {
            if(oid.equals(""))
            {
                objs.put("orders", orderService.findOrders_sid(own.uid));
                objs.put("code", 0);
                objs.put("message", "success");
            } else
            {
                objs.put("order", orderService.findOrder(checkoid));
                objs.put("code", 0);
                objs.put("message", "success");
            }
        } else if (own.role == Role.delivery)
        {
            if(oid.equals(""))
            {
                //待修改
                List<Order> orders = orderService.findOrders();
                orders.removeIf(order -> order.state == State.unpaid || order.state == State.paid);
                objs.put("orders", orders);
                objs.put("code", 0);
                objs.put("message", "success");
            } else
            {
                objs.put("order", orderService.findOrder(checkoid));
                objs.put("code", 0);
                objs.put("message", "success");
            }
        } else if(own.role==Role.buyer)
        {
            if(oid.equals(""))
            {
                objs.put("orders", orderService.findOrders_bid(own.uid));
                objs.put("code", 0);
                objs.put("message", "success");
            } else
            {
                objs.put("order", orderService.findOrder(checkoid));
                objs.put("code", 0);
                objs.put("message", "success");
            }
        }
        return objs;
    }

    /**
     * 添加 order
     * 仅当 role 为 buyer 时才能创建订单
     * @param para 为页面传进的 json 对应值
     * @return 返回对应信息和信息体
     * @author snowscattered
     */
    @RequestMapping("/add")
    @ResponseBody
    public Object addorder(@RequestBody Para para)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        Order order = para.order;
        List<Listing> listings = para.listings;
        String token= para.token==null?"": para.token;
        Info info=tokenPool.pool.get(token);
        if (info == null)
        {
            objs.put("code", "");
            objs.put("message", "非法操作");
            return objs;
        }
        User own= info.user;
        //
        if (own.role != Role.buyer)
        {
            objs.put("code", "");
            objs.put("message", "非法操作");
            return objs;
        }
        if(order==null||listings==null)
        {
            objs.put("code","");
            objs.put("message","目标不存在");
            return objs;
        }

        int oid=order.oid;
        for(Listing listing:listings)
            listing.oid=oid;
        if (orderService.addOrder(order) == 1 && listingService.addListings(listings) == 1)
        {
            objs.put("code", "");
            objs.put("message", "success");
        } else
        {
            try{
                orderService.delOrder(oid);
            }catch (Exception e){}
            objs.put("code", "");
            objs.put("message", "order异常");
        }
        return objs;
    }

    //
    /**
     * 更改 order
     * 当 role 为 admin,可以对指定的订单做出修改
     * 当 role 为 shop 时,仅变更当前指定的订单
     * 当 role 为 delivery 时,仅变更商家接单的订单
     * 当 role 为 buyer 时,仅可以进行支付订单
     * 可以看出:当 role 不为 admin 时,仅能变更 state,state 的变化于网页实现
     * @param para 为页面传进的 json 对应值
     * @return 返回对应信息和信息体
     * @author snowscattered
     */
    @RequestMapping("/update")
    @ResponseBody
    public Object updataorder(@RequestBody Para para)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        Order order = para.order;
//        String oid= para.oid == null ?"":para.oid;
//        State state= para.state == null ? null:State.valueOf(para.state);
        String token= para.token==null?"": para.token;
        Info info=tokenPool.pool.get(token);
        if (info == null)
        {
            objs.put("code", 10021);
            objs.put("message", "非法操作");
            return objs;
        }
        User own= info.user;

        if (order == null)
        {
            objs.put("code", 10025);
            objs.put("message", "不正确的order");
            return objs;
        }

        int status = orderService.updOrder(order);
        if (status == 1)
        {
            objs.put("code", 0);
            objs.put("message", "success");
        } else
        {
            objs.put("code", 10023);
            objs.put("message", "order异常");
        }
        return objs;
    }

    /**
     * 删除 order
     * 仅 admin 能删除已经完成交易的订单或者没有开始交易的订单,由页面控制删除内容
     * @param para 为页面传进的 json 对应值
     * @return 返回对应信息和信息体
     * @author snowscattered
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object deleteorder(@RequestBody Para para)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        String oid = para.oid;

        int checkoid;
        try
        {
            checkoid = Integer.parseInt(oid);
        } catch (Exception e)
        {
            objs.put("code", 10022);
            objs.put("message", "不正确的oid");
            return objs;
        }

        Order order = orderService.findOrder(checkoid);
        if (order.state != State.unpaid && order.state != State.finish)
        {
            objs.put("code", 10021);
            objs.put("message", "非法操作");
        } else
        {
            if (orderService.delOrder(checkoid) == 1)
            {
                objs.put("code", 0);
                objs.put("message", "success");
            } else
            {
                objs.put("code", 10023);
                objs.put("message", "order异常");
            }
        }
        return objs;
    }
}
