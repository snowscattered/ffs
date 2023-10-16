package com.ffs.controller.Review;

import com.ffs.cache.Info;
import com.ffs.cache.TokenPool;
import com.ffs.cache.UserCache;
import com.ffs.po.Review;
import com.ffs.po.Role;
import com.ffs.po.User;
import com.ffs.service.ReviewService;
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
    public String pid;
    public String oid;
    public String rid;
    public Review review;
}

@Controller
@RequestMapping("${baseURL}"+"api/review")
public class ReviewAPI
{
    @Autowired
    ReviewService reviewService;

    /**
     * 查找 review
     * 当 role 为 admins 时,可以查看所有或者某个商品的评论
     * 当 role 为 shop 时,可以查看自身对应商品的所有评论
     * 当 role 为 buyer 时,可以查看对应商品的所有评论
     * @param para 为页面传进的 json 对应值
     * @return 返回对应信息和信息体
     * @author snowscattered
     */
    @RequestMapping("/get")
    @ResponseBody
    public Object getReview(@RequestBody Para para)
    {
        Map<String,Object> objs=new LinkedHashMap<>();
        String pid= para.pid==null?"": para.pid;
        String oid= para.oid==null?"": para.oid;
        String token= para.token==null?"": para.token;
        User own = UserCache.getUser(token);
        if (own == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作1");
            return objs;
        }

        int checkpid = 0,checkoid=0;
        if(!pid.equals(""))
        {
            try
            {
                checkpid=Integer.parseInt(pid);
            }catch (Exception e)
            {
                objs.put("code","");
                objs.put("message","不正确的pid");
                return objs;
            }
        }
        if(!oid.equals(""))
        {
            try
            {
                checkoid=Integer.parseInt(oid);
            }catch (Exception e)
            {
                objs.put("code","");
                objs.put("message","不正确的oid");
                return objs;
            }
        }

        if(own.role== Role.admin)
        {
            if(!pid.equals(""))
            {
                objs.put("reviews",reviewService.findReviews());
                objs.put("code","");
                objs.put("message","success");
            }
            else
            {
                objs.put("reviews",reviewService.findReviews(checkpid));
                objs.put("code","");
                objs.put("message","success");
            }
        }
        else if(own.role==Role.shop)
        {
            objs.put("reviews",reviewService.findReviews(checkpid));
            objs.put("code","");
            objs.put("message","success");
        }
        else if(own.role==Role.buyer)
        {
            if(!oid.equals("")&&!pid.equals(""))
            {
                objs.put("reviews",reviewService.findReview(checkoid,checkpid));
                objs.put("code","");
                objs.put("message","success");
            }
            else if(!pid.equals(""))
            {
                objs.put("reviews", reviewService.findReviews(checkpid));
                objs.put("code", "");
                objs.put("message", "success");
            }
        }
        return objs;
    }

    /**
     * 添加 review
     * 仅当 role 为 buyer 时能发表评论
     * @param para 为页面传入的 json 对应值
     * @return 返回对应信息和信息体
     * @author scnowscatterd
     */
    @RequestMapping("/add")
    @ResponseBody
    public Object addReview(@RequestBody Para para)
    {
        Map<String,Object> objs=new LinkedHashMap<>();
        Review review= para.review;
        String token= para.token==null?"": para.token;
        User own = UserCache.getUser(token);
        if (own == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作1");
            return objs;
        }

        if(own.role!=Role.buyer)
        {
            objs.put("code", "");
            objs.put("message", "非法操作");
            return objs;
        }
        if(review==null)
        {
            objs.put("code","");
            objs.put("message","review错误");
            return objs;
        }

        int status= reviewService.addReview(review);
        if(status==1)
        {
            objs.put("code","");
            objs.put("message","success");
        }
        else
        {
            objs.put("code","");
            objs.put("messge","review异常");
        }
        return objs;
    }

    /**
     * 删除 review
     * 仅当 role 为 admin 和 buyer 时能删除评论
     * @param para 为页面传入的 json 对应值
     * @return 返回对应信息和信息体
     * @author scnowscatterd
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object deleteReview(@RequestBody Para para)
    {
        Map<String,Object> objs=new LinkedHashMap<>();
        String rid= para.rid==null?"": para.rid;
        String token= para.token==null?"": para.token;
        User own = UserCache.getUser(token);
        if (own == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作1");
            return objs;
        }

        if(own.role==Role.shop||own.role==Role.delivery)
        {
            objs.put("code", "");
            objs.put("message", "非法操作");
            return objs;
        }

        int checkrid;
        try
        {
            checkrid=Integer.parseInt(rid);
        }catch (Exception e)
        {
            objs.put("code","");
            objs.put("message","不正确的rid");
            return objs;
        }

        if(reviewService.delReview(checkrid)==1)
        {
            objs.put("code","");
            objs.put("message","success");
        }
        else
        {
            objs.put("code","");
            objs.put("message","review异常");
        }
        return objs;
    }
}
