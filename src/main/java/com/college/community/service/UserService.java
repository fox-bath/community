package com.college.community.service;

import com.college.community.entity.LoginTicket;
import com.college.community.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface UserService {
    //一般都加上@Param比较保险

    User selectById(@Param("id")int id);

    User selectByName(@Param("username")String username);

    User selectByEmail(@Param("email")String email);

    int insertUser(User user);

    int updateStatus(@Param("id")int id, @Param("status")int status);

    //更新用户头像
    int updateHeader(@Param("id")int id, @Param("headerUrl")String headerUrl);

    //修改密码
    int updatePassword(@Param("id")int id,@Param("password") String password);

    //注册，Map有利于返回多种错误的返回结果
    Map<String,Object> register(@Param("user") User user);

    int activation(int userId,String code);

    //登录
    Map<String,Object> login(String username,String password,int expiredSeconds);

    //退出账号
    void logout(String ticket);

    //查询ticket
    LoginTicket findLoginTicket(String ticket);


}
