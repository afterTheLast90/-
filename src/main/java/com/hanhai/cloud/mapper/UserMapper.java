package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-25-10:19
 **/
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user where (user_phone=#{p1} or user_email=#{p1}) and deleted=false ")
    public User getUserByPhoneOrEmail(@Param("p1") String t1);

    @Select("select * from user where user_phone=#{p1}  and deleted=false ")
    public User getUserByPhone(@Param("p1") String t1);
    @Select("select * from user where user_email=#{p1} and deleted=false ")
    public User getUserByEmail(@Param("p1") String t1);




    @Select("select * from user where (user_phone like concat('%',#{key} ,'%') or " +
            "user_email like concat('%',#{key} ,'%')  or user_name  like concat('%',#{key} ,'%'))" +
            "and deleted=false  ")
    public List<User> selectUser(@Param("key") String key);


    @Select("select * from user where deleted=false  ")
    public List<User> getAll();

    // 根据 email/phone/name模糊搜索用户，不包自身
    @Select("select user_id, user_name, user_email, user_phone from user where " +
            "(user_name like concat('%',#{userName},'%') or " +
            "user_email like concat('%',#{userName},'%') or " +
            "user_phone like concat('%',#{userName},'%')) and " +
            "deleted=false and user_id!=#{userId} limit 10")
    public List<User> getUserByName(@Param("userName")String userName, @Param("userId")Long userId);

    // 得到用户剩余空间大小
    @Select("select space_size-used_size from user where user_id=#{userId}")
    public Long getRemainSpace(@Param("userId") Long userId);
}
