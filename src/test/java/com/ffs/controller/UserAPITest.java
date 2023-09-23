package com.ffs.controller;

import com.ffs.controller.User.UserAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
public class UserAPITest
{
    @Autowired
    UserAPI userAPI;

    MockMvc mockMvc;
    @Test
    public void test()
    {

    }
}
