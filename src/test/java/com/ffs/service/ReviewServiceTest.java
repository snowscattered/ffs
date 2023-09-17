package com.ffs.service;

import com.ffs.po.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * ReviewService 的测试方法
 * @author hoshinosena
 * @version 1.0
 */
@SpringBootTest
public class ReviewServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    OrderService orderService;
    @Autowired
    ProductService productService;
    @Autowired
    ListingService listingService;
    @Autowired
    ReviewService reviewService;

    @Test
    public void test() throws Exception {
        List<User> users = userService.findUsers();
        for (int i=users.size()-1; 0<=i; i--) {
            userService.delUser(users.get(i).uid);
        }
        List<Order> orders = orderService.findOrders();
        for (int i=orders.size()-1; 0<=i; i--) {
            orderService.delOrder(orders.get(i).oid);
        }
        List<Product> products = productService.findProducts();
        for (int i=products.size()-1; 0<=i; i--) {
            productService.delProduct(products.get(i).pid);
        }
        List<Listing> listings = listingService.findListings();
        for (int i=listings.size()-1; 0<=i; i--) {
            listingService.delListing(listings.get(i).lid);
        }
        List<Review> reviews = reviewService.findReviews();
        for (int i=reviews.size()-1; 0<=i; i--) {
            reviewService.delReview(reviews.get(i).rid);
        }

        Review review = addReview();
        findReview();
        updReview(review);
        delReview();
    }

    public Review addReview() throws Exception {
        User user = new User();
        user.username = "test";
        user.password = "test";
        user.name = "test";
        user.role = Role.buyer;
        userService.addUser(user);
        user = userService.findUser("test");
        Product product = new Product();
        Product product1 = new Product();
        product.uid = user.uid;
        product1.uid = user.uid;
        product.name = "test";
        product1.name = "test1";
        product.image = "test";
        product1.image = "test1";
        product.price = 1145.14;
        product1.price = 19.19;
        productService.addProduct(product);
        productService.addProduct(product1);
        product = productService.findProducts("test").get(0);
        product1 = productService.findProducts("test1").get(0);
        Order order = new Order();
        order.bid = user.uid;
        orderService.addOrder(order);
        order = orderService.findOrders().get(0);
        Listing listing = new Listing();
        listing.oid = order.oid;
        listing.pid = product.pid;
        listing.uid = user.uid;
        listing.amount = 1;
        Listing listing1 = new Listing();
        listing1.oid = order.oid;
        listing1.pid = product1.pid;
        listing1.uid = user.uid;
        listing1.amount = 10;
        Listing listing2 = new Listing();
        listing2.oid = order.oid;
        listing2.pid = product.pid;
        listing2.uid = user.uid;
        listing2.amount = 2;
        List<Listing> listings = new ArrayList<>();
        listings.add(listing);
        listings.add(listing1);
        listings.add(listing2);
        listingService.addListings(listings);
        Review review = new Review();
        if (reviewService.addReview(review) != -1) {
            throw new Exception();
        }
        review.score = 50;
        if (reviewService.addReview(review) != -2) {
            throw new Exception();
        }
        review.detail = "test";
        if (reviewService.addReview(review) != 0) {
            throw new Exception();
        }
        review.oid = order.oid;
        if (reviewService.addReview(review) != 0) {
            throw new Exception();
        }
        review.pid = product.pid;
        if (reviewService.addReview(review) != 1) {
            throw new Exception();
        }
        review.score = 40;
        review.detail = "test1";
        reviewService.addReview(review);
        review.pid = product1.pid;
        review.score = 30;
        review.detail = "test2";
        reviewService.addReview(review);
        if (reviewService.findReviews().size() != 3) {
            throw new Exception();
        } else if (reviewService.findReviews(product.pid).size() != 2) {
            throw new Exception();
        } else if (reviewService.findReviews(product1.pid).size() != 1) {
            throw new Exception();
        }
        review = reviewService.findReview(order.oid, product1.pid);
        if (review.score != 30) {
            throw new Exception();
        } else if (!"test2".equals(review.detail)) {
            throw new Exception();
        }

        return review;
    }

    public void findReview() throws Exception {
        List<Review> reviews = reviewService.findReviews();
        for (int i=0; i<reviews.size(); i++) {
            if (reviewService.findReviews(reviews.get(i).rid) == null) {
                throw new Exception();
            }
        }
    }

    public void updReview(Review review) throws Exception {
        List<Review> reviews = reviewService.findReviews();
        for (int i=reviews.size()-1; 0<=i; i--) {
            reviewService.delReview(reviews.get(i).rid);
        }
        review.score = 50;
        review.detail = "test";
        reviewService.addReview(review);
        Review review1 = reviewService.findReviews().get(0);
        review1.score = 35;
        review1.detail = null;
        if (reviewService.updReview(review1) != 1) {
            throw new Exception();
        }
        review1 = reviewService.findReviews(review1.pid).get(0);
        if (review1.score != 35) {
            throw new Exception();
        } else if (!"test".equals(review1.detail)) {
            throw new Exception();
        }
        review1.detail = "testtest";
        reviewService.updReview(review1);
        review1 = reviewService.findReviews(review1.pid).get(0);
        if (!"testtest".equals(review1.detail)) {
            throw new Exception();
        }
    }

    public void delReview() throws Exception {
        List<Review> reviews = reviewService.findReviews();
        for (int i=reviews.size()-1; 0<=i; i--) {
            reviewService.delReview(reviews.get(i).rid);
        }
        if (reviewService.findReviews().size() != 0) {
            throw new Exception();
        }
    }
}
