package com.ffs.controller.Review;

import com.ffs.cache.UserCache;
import com.ffs.controller.Generation.BaseParameter;
import com.ffs.controller.User.UserAPI;
import com.ffs.po.*;
import com.ffs.service.OrderService;
import com.ffs.service.ReviewService;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ReviewAPI 处理 review 表的增删改查请求
 * 仅 POST 方法
 * @author hoshinosena
 * @version 1.0
 */
@Controller
@RequestMapping("${baseURL}" + "api/review/")
public class ReviewAPI {
    @Value("${selectBlockSize}")
    int blockSize;
    @Autowired
    UserService userService;
    @Autowired
    OrderService orderService;
    @Autowired
    ReviewService reviewService;

    private User preprocessingToken(BaseParameter p, Map<String, Object> r) {
        return UserAPI.preprocessingToken(p, r);
    }

    private User userAuthentication(Parameter p, Map<String, Object> r) {
        User u = UserAPI.preprocessingToken(p, r);

        if (u == null) {
            return null;
        }
        // 非用户拒绝执行
        if (!Role.buyer.equals(u.role)) {
            r.put("code", -40023);
            r.put("message", "拒绝执行");
            return null;
        }

        return u;
    }

    private User checkShop(Parameter p, Map<String, Object> r) {
        int uid;
        User u;

        // uid 错误
        try {
            uid = Integer.parseInt(p.uid);
        } catch (Exception e) {
            r.put("code", -40011);
            r.put("message", "不正确的uid");
            return null;
        }
        u = UserCache.getUser(uid);
        // 缓存未命中
        if (u == null) {
            u = userService.findUser(uid);
            if (u == null) {
                r.put("code", -40011);
                r.put("message", "不正确的uid");
                return null;
            }
            // 添加缓存
            UserCache.put(u);
        }
        if (!Role.shop.equals(u.role)) {
            r.put("code", -40022);
            r.put("message", "不正确的身份");
            return null;
        }

        return u;
    }

    private Review checkReview(Parameter p, Map<String, Object> r) {
        int rid;
        Review rv;

        // rid 错误
        try {
            rid = Integer.parseInt(p.rid);
        } catch (Exception e) {
            r.put("code", -40011);
            r.put("message", "不正确的rid");
            return null;
        }
        rv = reviewService.findReview(rid);
        if (rv == null) {
            r.put("code", -40011);
            r.put("message", "不存在该评论");
        }

        return rv;
    }

    @PostMapping("/get")
    @ResponseBody
    public Object getReview(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = preprocessingToken(p, r);
        if (user == null) {
            return r;
        }
        // 管理员允许全表查询
        if (Role.admin.equals(user.role) && p.rid == p.oid && p.rid == p.uid) {
            PageInfo<Review> pageInfo = PageChunk.chunk(p.block, blockSize, new Assign<>() {
               @Override
               public List<Review> assign() {
                   return reviewService.findReviews();
               }
            });
            r.put("code", 0);
            r.put("message", "success");
            r.put("reviews", pageInfo.getList());
            r.put("block", pageInfo.getPageNum());
            r.put("blocks", pageInfo.getPages());
            return r;
        }
        // 查询优先级 rid > oid > uid
        if (p.rid == p.oid) {
            User u = checkShop(p, r);
            if (u == null) {
                return r;
            }
            PageInfo<Review> pageInfo = PageChunk.chunk(p.block, blockSize, new Assign<>() {
                @Override
                public List<Review> assign() {
                    return reviewService.findReviews(u.uid);
                }
            });
            r.put("code", 0);
            r.put("message", "success");
            r.put("reviews", pageInfo.getList());
            r.put("block", pageInfo.getPageNum());
            r.put("blocks", pageInfo.getPages());
            return r;
        }
        if (p.rid == null) {
            int oid;
            Review rv;
            // oid 错误
            try {
                oid = Integer.parseInt(p.oid);
            } catch (Exception e) {
                r.put("code", -41021);
                r.put("message", "不正确的oid");
                return r;
            }
            // 废弃的 uid
            rv = reviewService.findReview_oid(oid);
            if (rv == null) {
                r.put("code", -41021);
                r.put("message", "不正确的oid");
                return r;
            }
            r.put("code", 0);
            r.put("message", "success");
            r.put("review", rv);
            return r;
        }
        Review rv = checkReview(p, r);
        if (rv == null) {
            return r;
        }
        // oid/uid 验证采取 int -> string 避免异常
        if ((p.oid != null && !(rv.oid + "").equals(p.oid))
            || (p.uid != null && !(rv.uid + "").equals(p.uid))) {
            r.put("code", -41002);
            r.put("message", "不匹配的信息");
            return r;
        }
        r.put("code", 0);
        r.put("message", "success");
        r.put("review", rv);
        return r;
    }

    @PostMapping("/add")
    @ResponseBody
    public Object addReview(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = userAuthentication(p, r);
        if (user == null) {
            return r;
        }
        int oid;
        Integer score;
        Order o;
        // oid 错误
        try {
            oid = Integer.parseInt(p.oid);
        } catch (Exception e) {
            r.put("code", -42011);
            r.put("message", "不正确的oid");
            return r;
        }
        // score 错误
        try {
            score = Integer.parseInt(p.score);
        } catch (Exception e) {
            r.put("code", -42041);
            r.put("message", "不正确的评分");
            return r;
        }
        o = orderService.findOrder(oid);
        if (o == null) {
            r.put("code", -42011);
            r.put("message", "不存在该订单");
            return r;
        }
        if (p.uid != null && !(o.sid + "").equals(p.uid)) {
            r.put("code", -42002);
            r.put("message", "不匹配的信息");
            return r;
        }
        // 未完成的订单不允许评论
        if (!State.finish.equals(o.state)) {
            r.put("code", -42003);
            r.put("message", "拒绝执行");
            return r;
        }
        Review rv = new Review();
        rv.oid = o.oid;
        rv.uid = o.sid;
        rv.score = score < 1 ? 1 : 5 < score ? 5 : score;
        rv.detail = p.detail;
        int code = reviewService.addReview(rv);
        switch (code) {
            case 1: {
                // 获取新 review 信息
                // 废弃的 uid
                rv = reviewService.findReview_oid(o.oid);
                r.put("code", 0);
                r.put("message", "success");
                r.put("rid", rv.rid);
                break;
            } case -2: {
                r.put("code", -42011);
                r.put("message", "没有评论");
                break;
            } default: {
                r.put("code", -50005);
                r.put("message", "未知错误");
            }
        }

        return r;
    }

    @PostMapping("/upd")
    @ResponseBody
    public Object updReview(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = userAuthentication(p, r);
        if (user == null) {
            return r;
        }
        Review rv = checkReview(p, r);
        if (rv == null) {
            return r;
        }
        // oid/uid 验证采取 int -> string 避免异常
        if ((p.oid != null && !(rv.oid + "").equals(p.oid))
             || (p.uid != null && !(rv.uid + "").equals(p.uid))) {
            r.put("code", -43002);
            r.put("message", "不匹配的信息");
            return r;
        }
        Integer score;
        // score 错误
        try {
            score = Integer.parseInt(p.score);
        } catch (Exception e) {
            r.put("code", -43041);
            r.put("message", "不正确的评分");
            return r;
        }
        rv.score = score < 1 ? 1 : 5 < score ? 5 : score;
        rv.detail = p.detail;
        int code = reviewService.updReview(rv);
        if (code == 0) {
            r.put("code", -43011);
            r.put("message", "不存在该评论");
            return r;
        }

        // 获取新 review 信息
        // 废弃的 uid
        rv = reviewService.findReview_oid(rv.oid);
        r.put("code", 0);
        r.put("message", "success");
        r.put("rid", rv.rid);
        return r;
    }

    @PostMapping("/del")
    @ResponseBody
    public Object delReview(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = preprocessingToken(p, r);
        if (user == null) {
            return r;
        }
        // 管理员删除逻辑
        if (Role.admin.equals(user.role)) {
            Review rv = checkReview(p, r);
            if (rv == null) {
                r.put("code", -44011);
                r.put("message", "错误的rid");
                return r;
            }
            reviewService.delReview(rv.rid);
            r.put("code", 0);
            r.put("message", "success");
            return r;
        }
        // 用户删除逻辑
        if (Role.buyer.equals(user.role)) {
            Review rv = checkReview(p, r);
            if (rv == null) {
                r.put("code", -44011);
                r.put("message", "错误的rid");
                return r;
            }
            // review 存在必然有 order
            Order o = orderService.findOrder(rv.oid);
            if (o.bid != user.uid) {
                r.put("code", -44003);
                r.put("message", "不匹配的uid");
                return r;
            }
            reviewService.delReview(rv.rid);
            r.put("code", 0);
            r.put("message", "success");
            return r;
        }

        // 拒绝执行
        r.put("code", -44003);
        r.put("message", "拒绝执行");
        return r;
    }
}

class Parameter extends BaseParameter {
    public String block;
    public String rid;
    public String oid;
    public String uid;
    public String score;
    public String detail;
}
