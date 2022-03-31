package com.college.community.config;

import com.college.community.controller.intercepter.AlphaInterceptor;
import com.college.community.controller.intercepter.LoginRequiredInterceptor;
import com.college.community.controller.intercepter.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)
                //静态资源不拦截
                .excludePathPatterns("/**/*.css","/**/*.png","/**/*.jpeg")
                //添加必须要拦截的controller
                .addPathPatterns("/register","/login");

        registry.addInterceptor(loginTicketInterceptor)
                //静态资源不拦截
                .excludePathPatterns("/**/*.css","/**/*.png","/**/*.jpeg");

        registry.addInterceptor(loginRequiredInterceptor)
                //静态资源不拦截
                .excludePathPatterns("/**/*.css","/**/*.png","/**/*.jpeg");
    }
}
