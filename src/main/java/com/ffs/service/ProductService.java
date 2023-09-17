package com.ffs.service;

import com.ffs.po.Product;

import java.util.List;

/**
 * ProductService 封装了代理类 ProductMapper
 * 并在执行相应方法前进行数据检查确保返回值是预期的
 * @author hoshinosena
 * @version 1.0
 */
public interface ProductService {
    /**
     * 透过 pid 查找 product, 有且仅有此方法允许
     * 获取被删除的 product
     * @param pid product 的 pid 字段
     * @return null 或 product 引用
     * @author hoshinosena
     */
    Product findProduct(int pid);

    /**
     * 透过 uid 查找 product
     * @param uid product 的 uid 字段
     * @return 非 null 的 List<Product> 引用
     * @author hoshinosena
     */
    List<Product> findProducts(int uid);

    /**
     * 透过 name 模糊查找 product
     * @param name product 的 name 字段
     * @return 非 null 的 List<Product> 引用
     * @author hoshinosena
     */
    List<Product> findProducts(String name);

    /**
     * 获取所有 product
     * @return 非 null 的 List<Product> 引用
     * @author hoshinosena
     */
    List<Product> findProducts();

    /**
     * 添加 product
     * @param product 除了引用为 null 外能够
     * 检查所有数据错误, 可以透过返回值了解错误
     * 原因
     * @return  1: 添加成功
     *          0: product 不存在或来自数据库
     *          的错误
     *         -1: name 为 null 或空
     *         -2: image 为 null 或空
     *         -3: price 为 null
     * @author hoshinosena
     */
    int addProduct(Product product);

    /**
     * 更新 product, 透过插入新的 product 实现,
     * 这将更新 pid 并清除所有与之相关的 review
     * @param product null 字段对应的数据不会被
     *                更新, 除了引用为 null 外
     *                能够检查所有数据错误, 可以
     *                透过返回值了解错误原因
     * @return  1: 更新成功
     *          0: product 不存在或来自数据库
     *          的错误
     *         -1: name 为空
     *         -2: image 为空
     * @author hoshinosena
     */
    int updProduct(Product product);

    /**
     * 删除 product
     * @param pid product 的 uid 字段
     * @return 1: 删除成功
     *         0: pid 不存在或来自数据库的错误
     * @author hoshinosena
     */
    int delProduct(int pid);
}
