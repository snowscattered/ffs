package com.ffs;

import com.ffs.cache.UserCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;


@SpringBootApplication
public class FfsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FfsApplication.class, args);
		UserCache.enable();
		new IMG().mkdir();
	}
}
class IMG
{
	public void mkdir()
	{
		String path = new ApplicationHome(getClass()).getSource().getParentFile().getPath()+"/image/";
		File filePath=new File(path);
		if(!filePath.exists())
			filePath.mkdirs();
	}
}