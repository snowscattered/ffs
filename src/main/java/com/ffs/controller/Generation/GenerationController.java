package com.ffs.controller.Generation;

import com.ffs.cache.UserCache;
import com.ffs.po.Role;
import com.ffs.po.User;
import com.ffs.service.UserService;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

import java.awt.image.MultiResolutionImage;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("${baseURL}")
public class GenerationController
{
    @Value("${baseURL}")
    String baseURL;
    @Autowired
    UserService userService;
    CentralProcessor processor;
    GlobalMemory memory;
    static long startTime;
    RateLimiter rateLimiter;

    public GenerationController(@Value("${AccessControlLimit}") double limit) {
        processor = new SystemInfo().getHardware().getProcessor();
        memory = new SystemInfo().getHardware().getMemory();
        startTime = System.currentTimeMillis();
        rateLimiter = RateLimiter.create(limit);
    }

    @RequestMapping("/")
    public String toIndex(HttpServletRequest req, HttpServletResponse rsp) {
        if (!rateLimiter.tryAcquire()) {
            rsp.setStatus(403);
            return "error/403";
        }

        Object isLogin = req.getSession().getAttribute("isLogin");
        if (isLogin != null && isLogin.equals(true)) {
            if (req.getSession().getAttribute("op").equals("admin")) {
                return "redirect:" + baseURL + "admin.html";
            }
            else if (req.getSession().getAttribute("op").equals("shop")) {
                return "redirect:" + baseURL + "shop.html";
            }
            else if (req.getSession().getAttribute("op").equals("delivery")) {
                return "redirect:" + baseURL + "delivery.html";
            }
            return "redirect:" + baseURL + "buyer.html";
        }
        rsp.addCookie(new Cookie("baseURL", baseURL));
        return "redirect:" + baseURL + "index.html";
    }

    @PostMapping("/to_sign")
    @ResponseBody
    public Object toSign(@RequestBody Para para,
                         HttpServletRequest req,
                         HttpServletResponse rsp) {
        String username = para.username == null ? "" : para.username;
        String password = para.password == null ? "" : para.password;
        String name = para.name == null ? "" : para.name;
        Role role =Role.valueOf(para.role);
        Map<String, Object> objs = new LinkedHashMap<>();

        if (!rateLimiter.tryAcquire()) {
            objs.put("code", 60001);
            objs.put("message", "请求过载");
            return  objs;
        }
        User addUser = new User();
        addUser.username = username;
        addUser.password = password;
        addUser.name = name;
        addUser.role = role;
        addUser.image = "default.png";
        if (userService.addUser(addUser) == 1) {
            objs.put("code", 0);
            objs.put("message", "success");
        } else {
            objs.put("code", 10003);
            objs.put("message", "user异常");
        }
        return objs;
    }


    @PostMapping("/to_login")
    @ResponseBody
    public Object toLogin(@RequestBody Para para,
                          HttpServletRequest req,
                          HttpServletResponse rsp) {
        String username = para.username == null ? "" : para.username;
        String password = para.password == null ? "" : para.password;
        Map<String, Object> objs = new LinkedHashMap<>();

        if (!rateLimiter.tryAcquire()) {
            objs.put("code", 60001);
            objs.put("message", "请求过载");
            return  objs;
        }

        User checkUser = userService.findUser(username);
        if (checkUser == null || !checkUser.password.equals(password)) {
            objs.put("code", 10001);
            objs.put("message", "非法操作");
        } else {
            String token;
            synchronized (this) {
                if (UserCache.getToken(checkUser.uid)!=null) {
                    UserCache.remove(checkUser.uid);
                }
                UserCache.put(checkUser);
                token = UserCache.getToken(checkUser.uid);
            }
            req.getSession().setAttribute("isLogin", true);
            rsp.addCookie(new Cookie("username", username));
            rsp.addCookie(new Cookie("token", token));
            rsp.addCookie(new Cookie("uid", checkUser.uid + ""));
            rsp.addCookie(new Cookie("name", checkUser.name));
            rsp.addCookie(new Cookie("image",checkUser.image));
            rsp.addCookie(new Cookie("tel", checkUser.tel));
            rsp.addCookie(new Cookie("address", checkUser.address));
            rsp.addCookie(new Cookie("info", checkUser.info));
            objs.put("code", 0);
            objs.put("message", "success");
            if (checkUser.role== Role.admin) {
                req.getSession().setAttribute("op", "admin");
                objs.put("url", "admin.html");
            }
            else if(checkUser.role==Role.buyer) {
                req.getSession().setAttribute("op", "buyer");
                objs.put("url", "buyer.html");
            }
            else if(checkUser.role==Role.shop) {
                req.getSession().setAttribute("op", "shop");
                objs.put("url", "shop.html");
            }
            else{
                req.getSession().setAttribute("op", "delivery");
                objs.put("url", "delivery.html");
            }
        }
        return objs;
    }
    @PostMapping("/to_guest")
    @ResponseBody
    public Object toGuest(HttpServletRequest req,
                          HttpServletResponse rsp) {
        Map<String, Object> objs = new LinkedHashMap<>();
        if (!rateLimiter.tryAcquire()) {
            objs.put("code", 60001);
            objs.put("message", "请求过载");
            return  objs;
        }
        objs.put("code", 0);
        req.getSession().setAttribute("op","guest");
        objs.put("url", "guest.html");
        return  objs;
    }

    @RequestMapping("/to_logout")
    @ResponseBody
    public void toLogout(@RequestBody Para para,
                         HttpServletResponse res,
                         HttpServletRequest req) {
        if (!rateLimiter.tryAcquire()) {
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        UserCache.delUser(para.token);
        session.removeAttribute("isLogin");
        session.removeAttribute("op");
    }

    @RequestMapping("/upload")
    @ResponseBody
    public Object upload(MultipartFile file) {
        Map<String, Object> objs=new LinkedHashMap<>();
        if (file.isEmpty())
        {
            objs.put("code",50001);
            objs.put("message","文件不存在");
            return objs;
        }
        String originalFilename = file.getOriginalFilename();
        String ext = "." + originalFilename.split("\\.")[1];
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0,25);
        String fileName = uuid + ext;
        String path = new ApplicationHome(getClass()).getSource().getParentFile().getPath()+"/image/";
        try{
            file.transferTo(new File(path+fileName));
        }catch (IOException e){
            e.printStackTrace();
        }
        objs.put("path",path);
        objs.put("fileName",fileName);
        objs.put("code",0);
        objs.put("message","success");
        return objs;
    }

    @PostMapping("/system/get")
    @ResponseBody
    public Object sys(@RequestBody Para p) throws InterruptedException {
        Map<String, Object> objs = new LinkedHashMap<>();

        if (p.token == null || "".equals(p.token)) {
            objs.put("code", -1);
            objs.put("message", "test");
            return null;
        }
        User u = UserCache.getUser(p.token);
        if (u == null) {
            objs.put("code", -1);
            objs.put("message", "test");
        }

        objs.put("code", 0);
        objs.put("message", "success");

        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 睡眠1s
        TimeUnit.SECONDS.sleep(1);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
                - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
                - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
                - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()]
                - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
                - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
                - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        objs.put("start", startTime);
        objs.put("tokens", UserCache.getTokenNums());
        objs.put("cpu", 100 - idle * 100.0 / totalCpu);
        objs.put("mem", 100 - memory.getAvailable() * 100.0 / memory.getTotal());
        objs.put("time", System.currentTimeMillis());

        return  objs;
    }
}
