package com.college.community.service.impl;

import com.college.community.dao.LoginTicketMapper;
import com.college.community.dao.UserMapper;
import com.college.community.entity.LoginTicket;
import com.college.community.entity.User;
import com.college.community.service.UserService;
import com.college.community.util.CommunityConstant;
import com.college.community.util.CommunityUtil;
import com.college.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.ACTIVITY_REQUIRED;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService, CommunityConstant {

    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private UserMapper userMapper;

    //发送邮箱工具类
    @Autowired
    private MailClient mailClient;

    //模板引擎
    @Autowired
    private TemplateEngine templateEngine;

    //项目地址
    @Value("${community.path.domain}")
    private String domain;

    //项目名
    @Value("${server.servlet.context-path}")
    private String contextPath;

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

    //更新头像
    @Override
    public int updateHeader(int id, String headerUrl) {
        return userMapper.updateHeader(id, headerUrl);
    }

    //修改密码
    @Override
    public int updatePassword(int id, String password) {

        return userMapper.updatePassword(id,password);
    }

    //注册功能,返回值使用Map可以返回多组信息Map返回报错信息就代表注册不成功
    @Override
    public Map<String, Object> register(User user) {
        Map<String,Object> map=new HashMap<>();

        //注册空值处理
        if (user==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("usernameMsg","密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("usernameMsg","邮箱不能为空！");
            return map;
        }

        //注册验证账号
        User u=userMapper.selectByName(user.getUsername());
        if (u!=null){
            map.put("usernameMsg","该账号已存在");
             return map;
        }

        //验证邮箱
        u=userMapper.selectByEmail(user.getEmail());
        if (u!=null){
            map.put("usernameMsg","该邮箱已存在");
            return map;
        }

        //注册用户
        //随机数只取5位
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        //默认普通用户
        user.setType(0);
        //默认未激活
        user.setStatus(0);
        //设置激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        //%d占位符,设置头像
        user.setHeaderUrl(String.format(contextPath+"/img/headImage/head%d.png",new Random().nextInt(3)));
        //设置创建时间
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //激活邮件
        Context context=new Context();
        context.setVariable("email",user.getEmail());
        //http://localhost:8080/community/activation/101/code
        String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content=templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

    //验证激活码
    @Override
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        //如果已经激活
        if (user.getStatus()==1){
            //返回重复激活
            return ACTIVATION_REPEAT;
        //激活码正确
        }else if (user.getActivationCode().equals(code)){
            //返回激活成功,账户状态改为1（已激活）
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
            //激活码错误
        }else {
            //返回激活失败
            return ACTIVATION_REPEAT;
        }
    }

    //登录
    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String,Object> map=new HashMap<>();

        //空值处理
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("usernameMsg","密码不能为空！");
            return map;
        }

        //验证账号
        User user=userMapper.selectByName(username);
        if (user==null){
            map.put("usernameMsg","该账户不存在!");
            return map;
        }

        //验证状态
        if (user.getStatus()==0){
            map.put("usernameMsg","该账号未激活");
            return map;
        }

        //验证密码
        password=CommunityUtil.md5(password+user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确！");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        //有效时间：延后多少秒
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        //返回ticket
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    //退出登录
    @Override
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket,1);
    }

    //查询ticket
    @Override
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }


}
