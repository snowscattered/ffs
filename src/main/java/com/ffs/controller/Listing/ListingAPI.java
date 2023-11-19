package com.ffs.controller.Listing;

import com.ffs.cache.UserCache;
import com.ffs.po.Listing;
import com.ffs.po.Role;
import com.ffs.po.User;
import com.ffs.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

class Para
{
    public String token;
    public Listing listing;
    public String oid;
    public String lid;

}

/**
 * 针对订单列表时的操作处理
 * @author snowscattered
 */
@Controller
@RequestMapping("${baseURL}"+"api/listing")
public class ListingAPI
{
    @Autowired
    ListingService listingService;

    /**
     * 查找 listing
     * 当 role 为 admins 时,可以查看所有或者指定的订单列表
     * 当 role 为 buyer 或者 delivery 时,可以查看对应订单的订单列表
     * @param para 为页面传进的 json 对应值
     * @return 返回对应信息和信息体
     * @author snowscattered
     */
    @RequestMapping("/get")
    @ResponseBody
    public Object getListing(@RequestBody Para para)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        String oid= para.oid==null?"": para.oid;
        String lid= para.lid==null?"": para.lid;
        String token= para.token==null?"": para.token;
        User own = UserCache.getUser(token);
        if (own == null) {
            objs.put("code", -1);
            objs.put("message", "非法操作1");
            return objs;
        }

        int checkoid=0,checklid=0;
        if(!oid.equals(""))
        {
            try
            {
                checkoid = Integer.parseInt(para.oid);
            } catch (Exception e)
            {
                objs.put("code", 10032);
                objs.put("message", "不正确的oid");
                return objs;
            }
        }
        if(!lid.equals(""))
        {
            try
            {
                checklid = Integer.parseInt(para.lid);
            } catch (Exception e)
            {
                objs.put("code", 10033);
                objs.put("message", "不正确的lid");
                return objs;
            }
        }

        if (own.role == Role.admin)
        {
            if (oid.equals("") && lid.equals(""))
            {
                objs.put("listings", listingService.findListings());
                objs.put("code", 0);
                objs.put("message", "success");
            } else if(!oid.equals(""))
            {
                objs.put("listings", listingService.findListingsByOid(checkoid));
                objs.put("code", 0);
                objs.put("message", "success");
            }
            else
            {
                objs.put("listing", listingService.findListing(checklid));
                objs.put("code", 0);
                objs.put("message", "success");
            }
        }
        else if(own.role==Role.buyer || own.role==Role.delivery)
        {
            objs.put("listings",listingService.findListingsByOid(checkoid));
            objs.put("code", 0);
            objs.put("message", "success");
        }
        return objs;
    }

    //是否选择在添加order时添加
    /**
     * 添加 listing
     * 当 role 为 admins 时,可以添加指定的订单列表
     * 当 role 为 buyer 时,可以在下单后添加对应订单的订单列表
     * @param para 为页面传进的 json 对应值
     * @return 返回对应信息和信息体
     * @author snowscattered
     */
    @RequestMapping("/add")
    @ResponseBody
    public Object addListing(@RequestBody Para para)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        Listing listing = para.listing;
        String token= para.token==null?"": para.token;
        User own = UserCache.getUser(token);
        if (own == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作1");
            return objs;
        }

        if (own.role != Role.admin)
        {
            objs.put("code", "");
            objs.put("message", "非法操作");
            return objs;
        }
        if (listing == null)
        {
            objs.put("code", "");
            objs.put("message", "非法操作");
            return objs;
        } else if (listingService.addListing(listing) == 1)
        {
            objs.put("code", "");
            objs.put("message", "success");
        } else
        {
            objs.put("code", "");
            objs.put("message", "listing异常");
        }
        return objs;
    }
    /**
     * 删除 listing
     * 仅当 role 为 admins 时,可以删除订单列表
     * @param para 为页面传进的 json 对应值
     * @return 返回对应信息和信息体
     * @author snowscattered
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object deleteListing(@RequestBody Para para)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        String lid = para.lid == null ? "" : para.lid;
        String token= para.token==null?"": para.token;
        User own = UserCache.getUser(token);
        if (own == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作1");
            return objs;
        }

        if (own.role!=Role.admin)
        {
            objs.put("code", "");
            objs.put("message", "非法操作");
            return objs;
        }
        int checklid=0;
        try
        {
            checklid=Integer.parseInt(lid);
        }
        catch (Exception e)
        {
            objs.put("code","");
            objs.put("message","不正确的lid");
            return objs;
        }

        if(listingService.delListing(checklid)==1)
        {
            objs.put("code","");
            objs.put("message","success");
        }
        else
        {
            objs.put("code","");
            objs.put("message","listing异常");
        }
        return objs;
    }
}