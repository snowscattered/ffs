package com.ffs.controller.Generation;

import com.ffs.cache.Info;
import com.ffs.cache.TokenPool;
import com.ffs.po.Role;
import com.ffs.po.User;
import com.ffs.service.UserService;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.MultiResolutionImage;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("${baseURL}")
public class GenerationController
{
    @Value("${baseURL}")
    String baseURL;
    @Autowired
    UserService userService;
    @Autowired
    TokenPool tokenPool;
    RateLimiter rateLimiter;

    public GenerationController(@Value("${AccessControlLimit}") double limit) {
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
            String token = UUID.randomUUID().toString();
            synchronized (this) {
                if (tokenPool.bind.containsKey(checkUser.uid)) {  // 刷新token
                    tokenPool.pool.remove(tokenPool.bind.get(checkUser.uid));
                }
                tokenPool.bind.put(checkUser.uid, token);
                tokenPool.pool.put(token, new Info(checkUser));
            }
            req.getSession().setAttribute("isLogin", true);
            rsp.addCookie(new Cookie("username", username));
            rsp.addCookie(new Cookie("token", token));
            rsp.addCookie(new Cookie("uid", checkUser.uid + ""));
            rsp.addCookie(new Cookie("name", checkUser.name));
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

    @RequestMapping("/to_logout")
    @ResponseBody
    public void toLogout(HttpServletRequest req) {
        if (!rateLimiter.tryAcquire()) {
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute("isLogin");
        session.removeAttribute("op");
    }

    @RequestMapping("/upload")
    @ResponseBody
    public Object upload(MultipartFile file)
    {
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
        ApplicationHome applicationHome = new ApplicationHome(this.getClass());
        String pre = applicationHome.getDir().getParentFile().getParentFile().getAbsolutePath() +
                "/src/main/resources/static/img/";
        String path=pre+fileName;
        try{
            file.transferTo(new File(path));
        }catch (IOException e){
            e.printStackTrace();
        }
        objs.put("path",path);
        objs.put("fileName",fileName);
        objs.put("code",0);
        objs.put("message","success");
        return objs;
    }

    @RequestMapping("/delete")
    public Object delete(String path)
    {
        Map<String, Object> objs=new LinkedHashMap<>();
        return objs;
    }

//    public Object abc(HttpServletResponse req) throws IOException
//    {
//        byte[] a = new byte[0];
//        OutputStream in = req.getOutputStream();
//        in.write(a, 0, 128);
//        if (129 <= a.length) {
//            return new HashMap<>();
//        }
//        String filename;
//
//        File file = new File(filename);
//        file.createNewFile();
//        FileOutputStream out = new FileOutputStream(file);
//        out.write(a);
//    }
}
