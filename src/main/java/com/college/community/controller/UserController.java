package com.college.community.controller;

import com.college.community.entity.User;
import com.college.community.service.UserService;
import com.college.community.util.CommunityUtil;
import com.college.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {
    //日志
    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    //域名
    @Value("${community.path.domain}")
    private String domain;

    //访问路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    //上传地址
    @Value("${community.path.upload}")
    private String uploadPath;


    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    //跳转到设置页面
    @RequestMapping(value = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    //上传图片
    //MultipartFile是MVC提供的对象，前端传几个值，就设置几个MultipartFile形参
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage==null){
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }
        //获取原始文件名
        String filename=headerImage.getOriginalFilename();
        //获取后缀
        String suffix=filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件的格式不正确!");
            return "/site/setting";
        }

        //生成随机文件名
        filename= CommunityUtil.generateUUID()+suffix;
        //确定文件存放的路径
        File dest=new File(uploadPath+"/"+filename);
        try {
            //把headerImage放到dest文件里
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败："+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发送异常",e);
        }

        //更新当前用户的头像路径(web访问路径)
        //http://localhost:8080/community/user/header/xxx.png
        User user=hostHolder.getUser();
        String headerUrl=domain+contextPath+"/user/header/"+filename;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    //获取头像
    @RequestMapping(value = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName=uploadPath+"/"+fileName;
        //文件后缀
        String suffix=fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+suffix);
        try(    //新语法，自动关闭流
                FileInputStream fis=new FileInputStream(fileName);
                OutputStream os=response.getOutputStream();
                )
        {
            byte[] buffer=new byte[1024];
            int b=0;
            //=-1就是没读到数据
            while ((b=fis.read(buffer))!=-1) {
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败："+e.getMessage());
        }
    }

    //修改密码
    @RequestMapping(value = "/changePassword",method = RequestMethod.POST)
    public String changePassword(String oldPassword,String newPassword,
                                 String confirmPassword,
                                 Model model){
        User user = hostHolder.getUser();
        if (StringUtils.isBlank(oldPassword)){
            model.addAttribute("oldPasswordMsg","密码不能为空!");
            return "/site/setting";
        }

        String password = CommunityUtil.md5(oldPassword + user.getSalt());
        String oldpassword = user.getPassword();
        if (!password.equals(oldpassword)){
            model.addAttribute("oldPasswordMsg","密码不正确!");
            return "/site/setting";
        }
        if (StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordMsg","新密码不能为空!");
            return "/site/setting";
        }
        if (newPassword.length()<8){
            model.addAttribute("newPasswordMsg","新密码长度不能小于8!");
            return "/site/setting";
        }
        if (!newPassword.equals(confirmPassword)){
            model.addAttribute("confirmPasswordMsg","两次输入的密码不一致!");
            return "/site/setting";
        }
        String s = CommunityUtil.md5(newPassword + user.getSalt());
        int i = userService.updatePassword(user.getId(), s);
        if (i>0){
            return "redirect:/index";
        }
        return "/site/setting";
    }
}
