package com.ffs.controller.OrderAPI;

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
    public User own;
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

    /**
     * 查找 order
     * 当 role 为 admin 时,可以查看所有或者指定的订单
     * 当 role 为 shop 时,仅查看自身的所有订单
     * 当 role 为 delivery 时,可以查看所有自身的订单或者被商家接单的订单
     * 当 role 为 buyer 时,可以查看自己所有或者指定的订单
     * @param pare 为页面传进的 json 对应值
     * @return 返回对应信息和信息体
     * @author snowscattered
     */
    @RequestMapping("/get")
    @ResponseBody
    public Object getorder(@RequestBody Para pare)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        User own = pare.own;
        String oid = pare.oid;

        if (own == null)
        {
            objs.put("code", "");
            objs.put("message", "非法操作");
            return objs;
        }

        int checksid = 0;
        if (!oid.equals(""))
        {
            try
            {
                checksid = Integer.parseInt(oid);
            } catch (Exception e)
            {
                objs.put("code", "");
                objs.put("message", "不正确的sid");
                return objs;
            }
        }

        if (own.role == Role.admin)
        {
            if (oid.equals(""))
            {
                objs.put("orders", orderService.findOrders());
                objs.put("code", "");
                objs.put("message", "success");
            } else
            {
                objs.put("orders", orderService.findOrders_sid(checksid));
                objs.put("code", "");
                objs.put("message", "success");
            }
        } else if (own.role == Role.shop)
        {
            objs.put("orders", orderService.findOrders_sid(own.uid));
            objs.put("code", "");
            objs.put("message", "success");
        } else if (own.role == Role.delivery)
        {
            //待修改
            List<Order> orders = orderService.findOrders();
            orders.removeIf(order -> order.state != State.received || order.did != own.uid);
            objs.put("orders", orders);
            objs.put("code", "");
            objs.put("message", "success");
        } else if(own.role==Role.buyer)
        {
            objs.put("orders", orderService.findOrders_bid(own.uid));
            objs.put("code", "");
            objs.put("message", "success");
        }
        return objs;
    }

    /**
     * 添加 order
     * 仅当 role 为 buyer 时才能创建订单
     * @param pare 为页面传进的 json 对应值
     * @return 返回对应信息和信息体
     * @author snowscattered
     */
    @RequestMapping("/add")
    @ResponseBody
    public Object addorder(@RequestBody Para pare)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        User own = pare.own;
        Order order = pare.order;
        List<Listing> listings = pare.listings;

        //
        if (own == null || own.role != Role.buyer)
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
     * @param pare 为页面传进的 json 对应值
     * @return 返回对应信息和信息体
     * @author snowscattered
     */
    @RequestMapping("/updata")
    @ResponseBody
    public Object updataorder(@RequestBody Para pare)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        User own = pare.own;
        Order order = pare.order;
        String oid= pare.oid;
        State state= State.valueOf(pare.state);

        if (own == null)
        {
            objs.put("code", "");
            objs.put("message", "非法操作");
            return objs;
        }
        if (order == null)
        {
            objs.put("code", "");
            objs.put("message", "不正确的order");
            return objs;
        }

        if (own.role == Role.admin)
        {
            int status = orderService.updOrder(order);
            if (status == 1)
            {
                objs.put("code", "");
                objs.put("message", "success");
            } else
            {
                objs.put("code", "");
                objs.put("message", "order异常");
            }
        }
        else
        {
            if(oid.equals(""))
            {
                objs.put("code", "");
                objs.put("message", "order异常");
                return objs;
            }else
            {
                int checkoid;
                try{
                    checkoid=Integer.parseInt(oid);
                }catch (Exception e)
                {
                    objs.put("code", "");
                    objs.put("message", "不正确的oid");
                    return objs;
                }
                Order o=orderService.findOrder(checkoid);
                o.state=state;
                orderService.updOrder(o);
                objs.put("code","");
                objs.put("message","success");
            }
        }
        return objs;
    }

    /**
     * 删除 order
     * 仅能删除已经完成交易的订单或者没有开始交易的订单,由页面控制删除内容
     * @param pare 为页面传进的 json 对应值
     * @return 返回对应信息和信息体
     * @author snowscattered
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object deleteorder(@RequestBody Para pare)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        String oid = pare.oid;

        int checkoid;
        try
        {
            checkoid = Integer.parseInt(oid);
        } catch (Exception e)
        {
            objs.put("code", "");
            objs.put("message", "不正确的oid");
            return objs;
        }

        Order order = orderService.findOrder(oid);
        if (order.state != State.unpaid && order.state != State.finish)
        {
            objs.put("code", "");
            objs.put("message", "非法操作");
        } else
        {
            if (orderService.delOrder(checkoid) == 1)
            {
                objs.put("code", "");
                objs.put("message", "success");
            } else
            {
                objs.put("code", "");
                objs.put("message", "order异常");
            }
        }
        return objs;
    }
}
