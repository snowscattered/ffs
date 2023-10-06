package com.ffs;

import com.ffs.cache.UserCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FfsApplication {
	public static void main(String[] args) {
		UserCache.enable();
		SpringApplication.run(FfsApplication.class, args);
	}
}
