package com.college.community.service.impl;

import com.college.community.dao.UserMapper;
import com.college.community.entity.User;
import com.college.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectById(int id) {
        return userMapper.selectById(id);
    }

    @Override
    public User selectByName(String username) {
        return null;
    }

    @Override
    public User selectByEmail(String email) {
        return null;
    }

    @Override
    public int insertUser(User user) {
        return 0;
    }

    @Override
    public int updateStatus(int id, int status) {
        return 0;
    }

    @Override
    public int updateHeader(int id, String headerUrl) {
        return 0;
    }

    @Override
    public int updatePassword(int id, String password) {
        return 0;
    }
}
