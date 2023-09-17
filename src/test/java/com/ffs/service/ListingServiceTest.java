package com.ffs.service;


import com.ffs.po.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ListingService 的测试方法
 * @author hoshinosena
 * @version 1.0
 */
@SpringBootTest
public class ListingServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    OrderService orderService;
    @Autowired
    ProductService productService;
    @Autowired
    ListingService listingService;

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

        listings = addListing();
        listings = findListing(listings);
        delListing(listings);
    }

    public List<Listing> addListing() throws Exception {
        User user = new User();
        user.username = "test";
        user.password = "test";
        user.name = "test";
        user.role = Role.shop;
        userService.addUser(user);
        user = userService.findUser("test");
        Order order = new Order();
        order.bid = user.uid;
        orderService.addOrder(order);
        order = orderService.findOrders().get(0);
        Product product = new Product();
        product.uid = user.uid;
        product.name = "test";
        product.image = "test";
        product.price = 1145.14;
        productService.addProduct(product);
        product = productService.findProducts(user.uid).get(0);
        Listing listing = new Listing();
        if (listingService.addListing(listing) != 0) {
            throw new Exception();
        }
        listing.uid = user.uid;
        if (listingService.addListing(listing) != 0) {
            throw new Exception();
        }
        listing.oid = order.oid;
        if (listingService.addListing(listing) != 0) {
            throw new Exception();
        }
        listing.pid = product.pid;
        listing.amount = 21;
        if (listingService.addListing(listing) != 1) {
            throw new Exception();
        }
        listing = listingService.findListings().get(0);
        if (listing.uid != user.uid) {
            throw new Exception();
        } else if (listing.oid != order.oid) {
            throw new Exception();
        } else if (listing.pid != product.pid) {
            throw new Exception();
        } else if (listing.amount != 21) {
            throw new Exception();
        }
        User user1 = new User();
        user1.username = "test1";
        user1.password = "test1";
        user1.name = "test1";
        user1.role = Role.shop;
        userService.addUser(user1);
        user1 = userService.findUser("test1");
        Order order1 = new Order();
        order1.bid = user1.uid;
        orderService.addOrder(order1);
        List<Order> orders = orderService.findOrders();
        order1 = Objects.equals(orders.get(0).bid, order.bid) ? orders.get(1) : orders.get(0);
        listing.oid = order1.oid;
        Listing listing1 = new Listing();
        listing1.uid = -1;
        listing1.oid = order1.oid;
        listing1.pid = product.pid;
        listing1.amount = 14;
        List<Listing> listings = new ArrayList<>();
        listings.add(listing);
        listings.add(listing1);
        listings.add(listing);
        if (listingService.addListings(listings) != 0) {
            throw new Exception();
        }
        listings.clear();
        listing1.uid = user.uid;
        for (int i=0; i<3; i++) {
            listings.add(listing1);
        }
        if (listingService.addListings(listings) != 3) {
            throw new Exception();
        }
        if (listingService.findListings().size() != 4) {
            throw new Exception();
        }
        listings.add(listing);
        return listings;
    }

    public List<Listing> findListing(List<Listing> listings) throws Exception {
        List<Listing> listings1 = listingService.findListings();
        for (int i=0; i<listings.size(); i++) {
            boolean flag = true;
            for (int j = 0; j < listings1.size(); j++) {
                if (listings.get(i).oid == listings1.get(j).oid) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                throw new Exception();
            }
        }

        return listings1;
    }

    public void delListing(List<Listing> listings) throws Exception {
        for (int i=listings.size()-1; 0<=i; i--) {
            listingService.delListing(listings.get(i).lid);
        }
        if (listingService.findListings().size() != 0) {
            throw new Exception();
        }
    }
}
