package com.college.community.service;

import com.college.community.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserService {
    //一般都加上@Param比较保险

    User selectById(@Param("id")int id);

    User selectByName(@Param("username")String username);

    User selectByEmail(@Param("email")String email);

    int insertUser(User user);

    int updateStatus(@Param("id")int id, @Param("status")int status);

    int updateHeader(@Param("id")int id, @Param("headerUrl")String headerUrl);

    int updatePassword(@Param("id")int id,@Param("password") String password);
}
