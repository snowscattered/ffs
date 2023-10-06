package com.ffs.service.impl;

import com.ffs.mapper.ReviewMapper;
import com.ffs.po.Review;
import com.ffs.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * ReviewServiceImpl 实现了 ReviewService
 * 详细信息在 ReviewService 接口中说明
 * @author hoshinosena
 * @version 1.1
 */
@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    ReviewMapper reviewMapper;

    @Override
    public Review findReview(int rid) {
        return reviewMapper.selectReviewByRid(rid);
    }

    @Override
    public Review findReview(int oid, int uid) {
        return reviewMapper.selectReviewByOU(oid, uid);
    }

    @Override
    public List<Review> findReviews(int uid) {
        return reviewMapper.selectReviewsByUid(uid);
    }

    @Override
    public List<Review> findReviews() {
        return reviewMapper.selectReviews();
    }

    @Override
    public int addReview(Review review) {
        if (review.score == null) {
            return -1;
        } else if (review.detail == null || "".equals(review.detail)) {
            return -2;
        }
        review.date = new Timestamp(System.currentTimeMillis()).toString();

        try {
            return reviewMapper.insertReview(review);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int updReview(Review review) {
        Review r = reviewMapper.selectReviewByRid(review.rid);
        if (r == null) {
            return 0;
        }
        if (review.score != null) {
            r.score = review.score;
        }
        if (review.detail != null && !"".equals(review.detail)) {
            r.detail = review.detail;
        }
        r.date = new Timestamp(System.currentTimeMillis()).toString();
//        if (reviewMapper.insertReview(r) == 1) {
//            reviewMapper.deleteReview(r.rid);
//            return 1;
//        }
//        return 0;
        reviewMapper.insertReview(r);
        reviewMapper.deleteReview(r.rid);
        return 1;
    }

    @Override
    public int delReview(int rid) {
        return reviewMapper.deleteReview(rid);
    }
}
