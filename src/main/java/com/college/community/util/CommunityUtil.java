package com.college.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

//方法很简单，不托管了，直接调用就行
public class CommunityUtil {

    //生成随机字符串给表中的字段salt
    public static String generateUUID(){
        //UUIDjava自带,
        // replaceAll("-","")：把-替换成空字符串
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密+表中的salt形成双重加密
    public static String md5(String key){
        //apache.commons.lang3自带，判断字符串是否为空，空字符串，空格
        if (StringUtils.isBlank(key)){
           return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
