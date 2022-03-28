package com.college.community.config;

import com.college.community.controller.intercepter.AlphaIntercepter;
import com.college.community.controller.intercepter.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AlphaIntercepter alphaIntercepter;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaIntercepter)
                //静态资源不拦截
                .excludePathPatterns("/**/*.css","/**/*.png","/**/*.jpeg")
                //添加必须要拦截的controller
                .addPathPatterns("/register","/login");

        registry.addInterceptor(loginTicketInterceptor)
                //静态资源不拦截
                .excludePathPatterns("/**/*.css","/**/*.png","/**/*.jpeg");
    }
}
