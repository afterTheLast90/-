package com.hanhai.cloud.service;

import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.User;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author wmgx
 * @create 2021-05-02-23:13
 **/
@Service
public class UserService extends BaseService {
    public User getUserByEmailAndPhone(String p){
        return userMapper.getUserByPhoneOrEmail(p);
    }


    @Cacheable(value = "userInfo",key = "#userId")
    public User getUserById(Long userId){
        return userMapper.selectById(userId);
    }


    @CachePut(value = "userInfo",key = "#user.userId")
    public User updateById(User user){
        userMapper.updateById(user);
        return  user;
    }


}
