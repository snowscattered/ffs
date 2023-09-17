package com.ffs.service;

import com.ffs.po.Product;
import com.ffs.po.Role;
import com.ffs.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * ProductService 的测试方法
 * @author hoshinosena
 * @version 1.0
 */
@SpringBootTest
public class ProductServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;

    @Test
    public void test() throws Exception {
        List<User> users = userService.findUsers();
        for (int i=users.size()-1; 0<=i; i--) {
            userService.delUser(users.get(i).uid);
        }
        List<Product> products = productService.findProducts();
        for (int i=products.size()-1; 0<=i; i--) {
            productService.delProduct(products.get(i).pid);
        }

        addProduct();
        findProduct();
        updProduct();
        delProduct();
    }

    public void addProduct() throws Exception {
        User user = new User();
        user.role = Role.shop;
        user.username = "test";
        user.password = "test";
        user.name = "test";
        userService.addUser(user);
        user = userService.findUser("test");
        Product product = new Product();
        product.uid = user.uid;
        if (productService.addProduct(product) != -1) {
            throw new Exception();
        }
        product.name = "test";
        if (productService.addProduct(product) != -2) {
            throw new Exception();
        }
        product.image = "";
        for (int i=0; i<8; i++) {
            product.image += "test";
        }
        if (productService.addProduct(product) != -3) {
            throw new Exception();
        }
        product.price = 1.5;
        if (productService.addProduct(product) != 1) {
            throw new Exception();
        }
    }

    public void findProduct() throws Exception {
        int uid = userService.findUser("test").uid;
        if (productService.findProducts(uid)== null) {
            throw new Exception();
        } else if (productService.findProducts(uid).size() != 1) {
            throw new Exception();
        } else if (productService.findProducts().size() != 1) {
            throw new Exception();
        }
        Product product = productService.findProducts(uid).get(0);
        if (!"test".equals(product.name)) {
            throw new Exception();
        }
        String image = "";
        for (int i=0; i<8; i++) {
            image += "test";
        }
        if (!image.equals(product.image)) {
            throw new Exception();
        } else if (product.price != 1.5) {
            throw new Exception();
        } else  if (product.score != 0) {
            throw new Exception();
        } else if (!"".equals(product.info)) {
            throw new Exception();
        }
    }

    public void updProduct() throws Exception {
        int uid = userService.findUser("test").uid;
        Product product = productService.findProducts(uid).get(0);
        product.image = "";
        if (productService.updProduct(product) != -2) {
            throw new Exception();
        }
        product.name = "";
        if (productService.updProduct(product) != -1) {
            throw new Exception();
        }
        product.name = "test name";
        product.image = null;
        product.price = 10.00;
        product.info = "test info";
        if (productService.updProduct(product) != 1) {
            throw new Exception();
        }
        product = productService.findProducts(uid).get(0);
        String image = "";
        for (int i=0; i<8; i++) {
            image += "test";
        }
        if (!image.equals(product.image)) {
            throw new Exception();
        } else if (10.00 != product.price) {
            throw new Exception();
        } else if (!"test info".equals(product.info)) {
            throw new Exception();
        }
        image = "";
        for (int i=0; i<2; i++) {
            image += "  test the image";
        }
        product.image = image;
        if (productService.updProduct(product) != 1) {
            throw new Exception();
        }
        product = productService.findProducts(uid).get(0);
        if (!image.equals(product.image)) {
            throw new Exception();
        }
    }

    public void delProduct() throws Exception {
        productService.delProduct(productService.findProducts("test name").get(0).pid);
        if (productService.findProducts().size() != 0) {
            throw new Exception();
        }
    }
}
