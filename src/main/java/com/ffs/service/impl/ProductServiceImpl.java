package com.ffs.service.impl;

import com.ffs.mapper.ProductMapper;
import com.ffs.po.Product;
import com.ffs.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ProductServiceImpl 实现了 ProductService
 * 详细信息在 ProductService 接口中说明
 * @author hoshinosena
 * @version 1.0
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductMapper productMapper;

    @Override
    public Product findProduct(int pid) {
        return productMapper.selectProductByPid(pid);
    }

    @Override
    public List<Product> findProducts(int uid) {
        return productMapper.selectProductsByUid(uid);
    }

    @Override
    public List<Product> findProducts(String name) {
        if ("".equals(name)) {
            return new ArrayList<>();
        }

        return productMapper.selectProductsByName(name);
    }

    @Override
    public List<Product> findProducts() {
        return productMapper.selectProducts();
    }

    @Override
    public int addProduct(Product product) {
        if (product.name == null || "".equals(product.name)) {
            return -1;
        } else if (product.image == null || "".equals(product.image)) {
            return -2;
        } else if (product.price == null) {
            return -3;
        }
        if (product.info == null) {
            product.info = "";
        }

        try {
            return productMapper.insertProduct(product);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int updProduct(Product product) {
        Product p;
        if ("".equals(product.name)) {
            return -1;
        } else if ("".equals(product.image)) {
            return -2;
        }
        p = productMapper.selectProductByPid(product.pid);
        if (p == null) {
            return 0;
        }
        if (product.name != null) {
            p.name = product.name;
        }
        if (product.image != null) {
            p.image = product.image;
        }
        if (product.price != null) {
            p.price = product.price;
        }
        if (product.info != null) {
            p.info = product.info;
        }
//        if (productMapper.insertProduct(p) == 1) {
//            productMapper.deleteProduct(p.pid);
//            return 1;
//        }
//        return 0;
        productMapper.insertProduct(p);
        productMapper.deleteProduct(p.pid);
        return 1;
    }

    @Override
    public int delProduct(int pid) {
        return productMapper.deleteProduct(pid);
    }
}
