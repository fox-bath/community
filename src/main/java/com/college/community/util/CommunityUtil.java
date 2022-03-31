package com.college.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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

    /**
     * 将数据封装成json
     * @param code 编码
     * @param msg  提示信息
     * @param map  业务数据
     * @return 返回json字符串
     */
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json=new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if (map!=null){
            for (String key: map.keySet()) {
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }

    //重载getJSONString
    public static String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }

    //重载getJSONString
    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

    public static void main(String[] args) {
        Map<String,Object> map=new HashMap<>();
        map.put("name","zhangsan");
        map.put("age",25);
        System.out.println(getJSONString(0,"ok",map));
    }
}
