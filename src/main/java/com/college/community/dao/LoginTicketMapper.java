package com.college.community.dao;

import com.college.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {

    //插入数据
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",//允许拼接，但是最好每个语句后面加空格
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    //自动生成主键，赋值给id
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    //查询数据，依据ticket
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //更新状态
    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int updateStatus(String ticket,int status);


}
