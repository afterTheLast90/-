package com.hanhai.cloud.service;

import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.User;
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
}
