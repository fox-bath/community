package com.college.community.controller.intercepter;

import com.college.community.annotation.LoginRequired;
import com.college.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Object handler要判断是不是方法，是方法才拦截，不拦截静态资源
        //HandlerMethod封装了很多属性，在访问请求方法的时候
        // 可以方便的访问到方法、方法参数、方法上的注解、所属类等
        // 并且对方法参数封装处理，也可以方便的访问到方法参数的注解等信息。
        //instanceof一般用于对象类型强制转换
        if (handler instanceof HandlerMethod){
            HandlerMethod handlerMethod=(HandlerMethod) handler;
            //获取拦截到的方法对象
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            //如果要登录你又没登录，就进行拦截
            if (loginRequired!=null&&hostHolder.getUser()==null){
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }
}
