package com.ffs.controller.Product;

import com.ffs.cache.Info;
import com.ffs.cache.TokenPool;
import com.ffs.cache.UserCache;
import com.ffs.po.Product;
import com.ffs.po.Role;
import com.ffs.po.User;
import com.ffs.service.ProductService;
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
    public String uid;
    public String name;
    public String pid;
    public String token;
    public Product product;
}
/**
 * 针对商品的操作处理
 * @author snowscattered
 */
@Controller
@RequestMapping("${baseURL}"+"api/product")
public class ProductAPI
{
    @Autowired
    ProductService productService;


    /**
     * 查找 Product
     * 当 role 为 admin 时,查看所有或者特定的商品
     * 当 role 为 shop 时,查看所有或者指定自身的商品
     * 当 role 为 delivery 时,查看订单列表里的商品
     * 当 role 为 buyer 时,查看所有或者指定商家的所有商品
     * @param para
     * @return 返回相应的 json
     * @author snowscattered
     */
    @RequestMapping("/get")
    @ResponseBody
    public Object getProduct(@RequestBody Para para)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        String uid = para.uid == null ? "" : para.uid;
        String name = para.name == null ? "" : para.name;
        String pid = para.pid == null ? "" : para.pid;
        String token=para.token==null? "" :para.token;
        User own = UserCache.getUser(token);
        if (own == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作1");
            return objs;
        }
        //初始化可能会有问题
        int checkuid = 0, checkpid = 0;
        if (!uid.equals(""))
        {
            try
            {
                checkuid = Integer.parseInt(uid);
            } catch (Exception e)
            {
                objs.put("code", 10012);
                objs.put("message", "不正确的uid");
                return objs;
            }
        }
        if (!pid.equals(""))
        {
            try
            {
                checkpid = Integer.parseInt(pid);
            } catch (Exception e)
            {
                objs.put("code", 10013);
                objs.put("message", "不正确的pid");
                return objs;
            }
        }
        if (own.role == Role.admin)
        {
            if (uid.equals("") && pid.equals("") && name.equals(""))
            {
                objs.put("products", productService.findProducts());
                objs.put("code", 0);
                objs.put("message", "success");
            } else if (!uid.equals(""))
            {
                objs.put("products", productService.findProducts(checkuid));
                objs.put("code", 0);
                objs.put("message", "success");
            } else if (!name.equals(""))
            {
                objs.put("products", productService.findProducts(name));
                objs.put("code", 0);
                objs.put("message", "success");
            } else if(!pid.equals(""))
            {
                objs.put("product", productService.findProduct(checkpid));
                objs.put("code", 0);
                objs.put("message", "success");
            }
        } else if (own.role == Role.shop)
        {
            if (pid.equals(""))
            {
                objs.put("products", productService.findProducts(own.uid));
                objs.put("code", 0);
                objs.put("message", "success");
            } else
            {
                objs.put("product", productService.findProduct(checkpid));
                objs.put("code", 0);
                objs.put("message", "success");
            }
        } else if (own.role == Role.delivery)
        {
            objs.put("product", productService.findProduct(checkpid));
            objs.put("code", 0);
            objs.put("message", "success");
        } else if(own.role==Role.buyer)
        {
            if(uid.equals(""))
            {
                objs.put("code", 10013);
                objs.put("message","未选择商家");
                return objs;
            }
            if(pid.equals("")&&name.equals(""))
            {
                objs.put("products",productService.findProducts(checkuid));
                objs.put("code",0);
                objs.put("message","success");
            }
            else if(!pid.equals(""))
            {
                Product product= productService.findProduct(checkpid);
                objs.put("product",product.uid==checkuid?"":product);
                objs.put("code",0);
                objs.put("message","success");
            }
            else if(!name.equals(""))
            {
                List<Product> products=productService.findProducts(name);
                for(Product product:products)
                    if(product.uid!=checkuid)
                        products.remove(product);
                objs.put("products",products);
                objs.put("code",0);
                objs.put("message","success");
            }
        }
        return objs;
    }

    /**
     * 添加 Product
     * 仅当 role 为 admin 或者 shop 时有权添加商品
     * @param para
     * @return 返回相应的 json
     * @author snowscattered
     */
    @RequestMapping("/add")
    @ResponseBody
    public Object addProduct(@RequestBody Para para)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        Product product = para.product;
        String token= para.token==null?"": para.token;
        User own = UserCache.getUser(token);
        if (own == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作1");
            return objs;
        }

        if (own.role == Role.delivery || own.role == Role.buyer)
        {
            objs.put("code", 10011);
            objs.put("message", "非法操作");
            return objs;
        }
        if (product == null)
        {
            objs.put("code", 10012);
            objs.put("message", "product错误");
            return objs;
        }

        //该函数有多个返回值
        int status = productService.addProduct(product);
        if (status == 1)
        {
            objs.put("code", 0);
            objs.put("message", "success");
        } else
        {
            objs.put("code", 10012);
            objs.put("message", "product异常");
        }
        return objs;
    }

    /**
     * 查找 Product
     * 仅当 role 为 admin 或者 shop 时有权更改商品
     * @param para
     * @return 返回相应的 json
     * @author snowscattered
     */
    @RequestMapping("/update")
    @ResponseBody
    public Object updataProduct(@RequestBody Para para)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        Product product = para.product;
        String token= para.token==null?"": para.token;
        User own = UserCache.getUser(token);
        if (own == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作1");
            return objs;
        }

        if (own.role == Role.delivery || own.role == Role.buyer)
        {
            objs.put("code", 10011);
            objs.put("message", "非法操作");
            return objs;
        }
        if (product == null)
        {
            objs.put("code", 10015);
            objs.put("message", "没有待修改商品");
            return objs;
        }

        //该函数有多个返回值
        int status = productService.updProduct(product);
        if (status == 1)
        {

            objs.put("code", 0);
            objs.put("message", "success");
        } else
        {
            objs.put("code", 10012);
            objs.put("message", "product异常");
        }
        return objs;
    }

    /**
     * 查找 Product
     * 仅当 role 为 admin 或者 shop 时有权删除商品
     * @param para
     * @return 返回相应的 json
     * @author snowscattered
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object deleteProduct(@RequestBody Para para)
    {
        Map<String, Object> objs = new LinkedHashMap<>();
        String pid = para.pid == null ? "" : para.pid;
        String token= para.token==null?"": para.token;
        User own = UserCache.getUser(token);
        if (own == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作1");
            return objs;
        }

        if (own.role == Role.delivery || own.role == Role.buyer)
        {
            objs.put("code", 10011);
            objs.put("message", "非法操作");
            return objs;
        }

        int checkpid;
        try
        {
            checkpid = Integer.parseInt(pid);
        } catch (Exception e)
        {
            objs.put("code", 10013);
            objs.put("message", "不正确的pid");
            return objs;
        }
        if (productService.delProduct(checkpid) == 0)
        {
            objs.put("code", 10012);
            objs.put("message", "product异常");
        } else
        {
            objs.put("code", 0);
            objs.put("message", "success");
        }
        return objs;
    }
}
