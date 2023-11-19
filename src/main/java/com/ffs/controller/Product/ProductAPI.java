package com.ffs.controller.Product;

import com.ffs.controller.Generation.BaseParameter;
import com.ffs.controller.User.UserAPI;
import com.ffs.po.Product;
import com.ffs.po.Role;
import com.ffs.po.User;
import com.ffs.service.ProductService;
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
 * ProductAPI 处理 product 表的增删改查请求
 * 仅 POST 方法
 * @author hoshinosena
 * @version 1.0
 */
@Controller
@RequestMapping("${baseURL}" + "api/product/")
public class ProductAPI {
    @Value("${selectBlockSize}")
    int blockSize;
    @Autowired
    ProductService productService;

    private User preprocessingToken(BaseParameter p, Map<String, Object> r) {
        return UserAPI.preprocessingToken(p, r);
    }

    private User shopAuthentication(Parameter p, Map<String, Object> r) {
        User u = UserAPI.preprocessingToken(p, r);

        if (u == null) {
            return null;
        }
        // 非商户拒绝执行
        if (!Role.shop.equals(u.role)) {
            r.put("code", -20023);
            r.put("message", "拒绝执行");
            return null;
        }
        // uid 验证采取 int -> string 避免异常
        if (p.uid != null && !(u.uid + "").equals(p.uid)) {
            r.put("code", -20013);
            r.put("message", "不正确的uid");
            return null;
        }

        return u;
    }

    @PostMapping("/get")
    @ResponseBody
    public Object getProduct(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = preprocessingToken(p, r);
        if (user == null) {
            return r;
        }
        // 登陆用户都可以查
        // 查询优先级 pid > uid > name > 全表查询(管理员)
        if (p.pid != null) {
            int pid;
            Product pd;
            // pid 错误
            try {
                pid = Integer.parseInt(p.pid);
            } catch (Exception e) {
                r.put("code", -21011);
                r.put("message", "不正确的pid");
                return r;
            }
            pd = productService.findProduct(pid);
            if (pd == null) {
                r.put("code", -21011);
                r.put("message", "不存在该商品");
                return r;
            }
            // uid 验证采取 int -> string 避免异常
            if (p.uid != null && !(pd.uid + "").equals(p.uid)) {
                r.put("code", -21002);
                r.put("message", "不正确的uid");
                return r;
            }
            r.put("code", 0);
            r.put("message", "success");
            r.put("product", pd);
            return r;
        }
        if (p.uid != null) {
            int uid;
            // uid错误
            try {
                uid = Integer.parseInt(p.uid);
            } catch (Exception e) {
                r.put("code", -21011);
                r.put("message", "不正确的uid");
                return r;
            }
            if (p.name != null) {
                PageInfo<Product> pageInfo = PageChunk.chunk(p.block, blockSize,  new Assign<>() {
                    @Override
                    public List<Product> assign() {
                        List<Product> products=productService.findProducts(uid);
                        products.removeIf(product -> !product.name.contains(p.name));
                        return products;
                    }
                });

                r.put("code", 0);
                r.put("message", "success");
                r.put("products", pageInfo.getList());
                r.put("block", pageInfo.getPageNum());
                r.put("blocks", pageInfo.getPages());
                return r;
            }
            PageInfo<Product> pageInfo = PageChunk.chunk(p.block, blockSize, new Assign<>() {
                @Override
                public List<Product> assign() {
                    return productService.findProducts(uid);
                }
            });
            r.put("code", 0);
            r.put("message", "success");
            r.put("products", pageInfo.getList());
            r.put("block", pageInfo.getPageNum());
            r.put("blocks", pageInfo.getPages());
            return r;
        }
        if (p.name != null) {
            PageInfo<Product> pageInfo = PageChunk.chunk(p.block, blockSize,  new Assign<>() {
                @Override
                public List<Product> assign() {
                    return productService.findProducts(p.name);
                }
            });
            r.put("code", 0);
            r.put("message", "success");
            r.put("products", pageInfo.getList());
            r.put("block", pageInfo.getPageNum());
            r.put("blocks", pageInfo.getPages());
            return r;
        }
        // 全表查询
        // 非管理员拒绝执行
        if (!Role.admin.equals(user.role)) {
            r.put("code", -21002);
            r.put("message", "拒绝执行");
            return r;
        }
        PageInfo<Product> pageInfo = PageChunk.chunk(p.block, blockSize, new Assign<>() {
            @Override
            public List<Product> assign() {
                return productService.findProducts();
            }
        });
        r.put("code", 0);
        r.put("message", "success");
        r.put("products", pageInfo.getList());
        r.put("block", pageInfo.getPageNum());
        r.put("blocks", pageInfo.getPages());
        return r;
    }

    @PostMapping("/add")
    @ResponseBody
    public Object addProduct(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = shopAuthentication(p, r);
        if (user == null) {
            return r;
        }
        // 检查本地 image 文件
        if (p.image != null) {

        }
        Double price;
        Product pd = new Product();
        // price 错误
        try {
            price = Double.parseDouble(p.price);
        } catch (Exception e) {
            r.put("code", -22051);
            r.put("message", "不正确的价格");
            return r;
        }
        pd.score=0;
        pd.uid = user.uid;
        pd.name = p.name;
        pd.image = p.image;
        pd.price = price;
        pd.info = p.info;
        System.out.println(p.score);
        try {
            pd.score=Integer.parseInt(p.score);
        }catch (Exception e) {
            e.printStackTrace();
            r.put("code", -22061);
            r.put("message", "不正确的评分");
            return r;
        }
        int code = productService.addProduct(pd);
        switch (code) {
            case 1: {
                r.put("code", 0);
                r.put("message", "success");
                // 姑且先用这样, 后边再改
                List<Product> pds = productService.findProducts(pd.uid);
                r.put("pid", pds.get(0).pid);
                break;
            } case 0: {
                r.put("code", -50005);
                r.put("message", "不存在该商品");
                break;
            } case -1: {
                r.put("code", -22031);
                r.put("message", "错误的名称");
                break;
            } default: {
                r.put("code", -22051);
                r.put("message", "错误的价格");
            }
        }

        return r;
    }

    @PostMapping("/upd")
    @ResponseBody
    public Object updProduct(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = shopAuthentication(p, r);
        if (user == null) {
            return r;
        }
        // 检查本地 image 文件
        if (p.image != null) {

        }
        int pid;
        Double price = null;
        Product pd = new Product();
        // pid 错误
        try {
            pid = Integer.parseInt(p.pid);
        } catch (Exception e) {
            r.put("code", -23011);
            r.put("message", "不正确的pid");
            return r;
        }
        // price 检查
        if (p.price != null) {
            // price 错误
            try {
                price = Double.parseDouble(p.price);
            } catch (Exception e) {
                r.put("code", -23051);
                r.put("message", "不正确的价格");
                return r;
            }
        }
        pd.pid = pid;
        pd.uid = user.uid;
        pd.name = p.name;
        pd.image = p.image;
        pd.price = price;
        pd.info = p.info;
        int code = productService.updProduct(pd);
        switch (code) {
            case 1: {
                r.put("code", 0);
                r.put("message", "success");
                // 姑且先用这样, 后边再改
                List<Product> pds = productService.findProducts(pd.uid);
                r.put("pid", pds.get(0).pid);
                break;
            } case 0: {
                r.put("code", -23011);
                r.put("message", "不存在该商品");
                break;
            } case -1: {
                r.put("code", -23031);
                r.put("message", "错误的名称");
                break;
            } default: {
                r.put("code", -23051);
                r.put("message", "错误的价格");
            }
        }

        return r;
    }

    @PostMapping("/del")
    @ResponseBody
    public Object delProduct(@RequestBody Parameter p) {
        Map<String, Object> r = new LinkedHashMap<>();

        User user = preprocessingToken(p, r);
        if (user == null) {
            return r;
        }
        // 管理员删除逻辑
        if (Role.admin.equals(user.role)) {
            int code;
            int pid;
            // pid 错误
            try {
                pid = Integer.parseInt(p.pid);
            } catch (Exception e) {
                r.put("code", -24011);
                r.put("message", "不正确的pid");
                return r;
            }
            if (p.uid == null) {
                code = productService.delProduct(pid);
            } else {
                int uid;
                // uid 错误
                try {
                    uid = Integer.parseInt(p.uid);
                } catch (Exception e) {
                    r.put("code", -24001);
                    r.put("message", "不正确的uid");
                    return r;
                }
                code = productService.delProduct(uid, pid);
            }
            if (code == 0) {
                r.put("code", -24011);
                r.put("message", "不存在该商品");
                return r;
            }
            r.put("code", 0);
            r.put("message", "success");
            return r;
        }
        // 商户删除逻辑
        user = shopAuthentication(p, r);
        if (user == null) {
            return r;
        }
        int code;
        int pid;
        // pid 错误
        try {
            pid = Integer.parseInt(p.pid);
        } catch (Exception e) {
            r.put("code", -24011);
            r.put("message", "不正确的pid");
            return r;
        }
        code = productService.delProduct(user.uid, pid);
        if (code == 0) {
            r.put("code", -21011);
            r.put("message", "不存在该商品");
            return r;
        }

        r.put("code", 0);
        r.put("message", "success");
        return r;
    }
}

class Parameter extends BaseParameter {
    public String block;
    public String pid;
    public String uid;
    public String name;
    public String image;
    public String price;
    public String info;
    public String score;
}
