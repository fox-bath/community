package com.college.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//可以写在方法上
@Target(ElementType.METHOD)
//运行时有效
@Retention(RetentionPolicy.RUNTIME)
//方法只要有这个注解标记LoginRequired，登录时才可以访问
public @interface LoginRequired {

}
