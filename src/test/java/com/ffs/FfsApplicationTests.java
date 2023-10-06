package com.ffs;

import com.ffs.cache.UserCache;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FfsApplicationTests {
	@Test
	void contextLoads() {
		UserCache.enable();
	}
}
