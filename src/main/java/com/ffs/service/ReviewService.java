package com.ffs.service;

import com.ffs.po.Review;

import java.util.List;

/**
 * ReviewService 封装了代理类 ReviewMapper
 * 并在执行相应方法前进行数据检查确保返回值是预期的
 * @author hoshinosena
 * @version 1.1
 */
public interface ReviewService {
    /**
     * 透过 rid 查找 review
     * @param rid review 的 rid 字段
     * @return null 或 review 引用
     * @author hoshinosena
     */
    Review findReview(int rid);

    /**
     * 透过 oid 和 uid 查找 review
     * @param oid order 的 oid 字段
     * @param uid user 的 uid 字段
     * @return null 或 review 引用
     * @author hoshinosena
     */
    Review findReview(int oid, int uid);

    /**
     * 透过 uid 查找 review
     * @param uid user 的 uid 字段
     * @return 非 null 的 List<Review> 引用
     * @author hoshinosena
     */
    List<Review> findReviews(int uid);

    /**
     * 获取所有 review
     * @return 非 null 的 List<Review> 引用
     * @author hoshinosena
     */
    List<Review> findReviews();

    /**
     * 添加 review
     * @param review 除了引用为 null 外能够
     *               检查所有数据错误
     * @return  1: 添加成功
     *          0: 来自数据库的错误
     *         -1: score 为空
     *         -2: detail 为空
     * @author hoshinosena
     */
    int addReview(Review review);

    /**
     * 更新 review, 透过插入新的 review 实现
     * 这将更新 rid
     * @param review 非 null 引用
     * @return 1: 更新成功
     *         0: review 不存在或来自数据库
     *         的错误
     * @author hoshinosena
     */
    int updReview(Review review);

    /**
     * 删除 review
     * @param rid review 的 rid 字段
     * @return 1: 更新成功
     *         0: rid 不存在或来自数据库的错误
     * @author hoshinosena
     */
    int delReview(int rid);
}
