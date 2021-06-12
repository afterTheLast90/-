package com.hanhai.cloud.service;

import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.User;
import com.hanhai.cloud.params.UserSearchParams;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-05-02-23:13
 **/
@Service
public class UserService extends BaseService {
    public User getUserByEmailAndPhone(String p){
        return userMapper.getUserByPhoneOrEmail(p);
    }


    @Cacheable(value = "userInfo1",key = "#userId")
    public User getUserById(Long userId){
        return userMapper.selectById(userId);
    }


    @CachePut(value = "userInfo1",key = "#user.userId")
    public User updateById(User user){
        userMapper.updateById(user);
        return  user;
    }

    public List<User> selectUser(UserSearchParams param){
        startPage(param);
        return userMapper.selectUser(param.getKey());
    }

    public List<User> getAll(){
        return userMapper.getAll();
    }
    public User insertUser(User user){
        userMapper.insert(user);
        return user;
    }

    public Boolean checkUserPhone(String phone){
        return userMapper.getUserByPhone(phone)==null  ;
    }
    public Boolean checkUserEmail(String email){
        return userMapper.getUserByEmail(email)==null;
    }


    public User getUserByPhone(String phone){
        return userMapper.getUserByPhone(phone) ;
    }
    public User getUserByEmail(String email){
        return userMapper.getUserByEmail(email);
    }
    
    public void deleteUser(Long userId){
        userMapper.deleteById(userId);
    }


}
