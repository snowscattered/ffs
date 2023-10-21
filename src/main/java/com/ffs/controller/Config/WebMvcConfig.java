package com.ffs.controller.Config;

import com.ffs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configurable
public class WebMvcConfig implements WebMvcConfigurer
{
    @Value("${baseURL}")
    String baseURL;

    @Value("")
    String path;
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new Interceptor(baseURL)).addPathPatterns("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/image/**").addResourceLocations("file:"+path);
    }
}
