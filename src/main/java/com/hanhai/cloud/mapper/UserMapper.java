package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author wmgx
 * @create 2021-04-25-10:19
 **/
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user where user_phone=#{p1} or user_email=#{p1} and deleted=false ")
    public User getUserByPhoneOrEmail(@Param("p1") String t1);
}
