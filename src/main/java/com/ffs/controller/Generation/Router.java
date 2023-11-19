package com.ffs.controller.Generation;

import com.ffs.service.UserService;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

@Controller
@RequestMapping("${baseURL}")
public class Router
{
    @Value("${baseURL}")
    String baseURL;
    @Autowired
    UserService userService;
    RateLimiter rateLimiter;

    public Router(@Value("${AccessControlLimit}") double limit) {
        rateLimiter = RateLimiter.create(limit);
    }

    @RequestMapping("/admin.html")
    public String adminPage(HttpServletRequest req, HttpServletResponse rsp)
    {
        if (!rateLimiter.tryAcquire())
        {
            rsp.setStatus(403);
            return "error/403";
        }

        Object isLogin = req.getSession().getAttribute("isLogin");
        if (((Boolean) true).equals(isLogin) && req.getSession().getAttribute("op").equals("admin"))
            return "admin";
        rsp.setStatus(403);
        return "error/403";
    }

    @RequestMapping("/buyer.html")
    public String buyerPage(HttpServletRequest req, HttpServletResponse rsp) {
        if (!rateLimiter.tryAcquire()) {
            rsp.setStatus(403);
            return "error/403";
        }

        Object isLogin = req.getSession().getAttribute("isLogin");
        if (((Boolean) true).equals(isLogin) && req.getSession().getAttribute("op").equals("buyer"))
            return "buyer";
        rsp.setStatus(403);
        return "error/403";
    }

    @RequestMapping("/shop.html")
    public String shopPage(HttpServletRequest req, HttpServletResponse rsp) {
        if (!rateLimiter.tryAcquire()) {
            rsp.setStatus(403);
            return "error/403";
        }

        Object isLogin = req.getSession().getAttribute("isLogin");
        if (((Boolean) true).equals(isLogin) && req.getSession().getAttribute("op").equals("shop"))
            return "shop";
        rsp.setStatus(403);
        return "error/403";
    }

    @RequestMapping("/delivery.html")
    public String deliveryPage(HttpServletRequest req, HttpServletResponse rsp) {
        if (!rateLimiter.tryAcquire()) {
            rsp.setStatus(403);
            return "error/403";
        }

        Object isLogin = req.getSession().getAttribute("isLogin");
        if (((Boolean) true).equals(isLogin) && req.getSession().getAttribute("op").equals("delivery"))
            return "delivery";
        rsp.setStatus(403);
        return "error/403";
    }
}
