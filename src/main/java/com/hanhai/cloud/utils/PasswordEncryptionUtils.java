package com.hanhai.cloud.utils;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码加密的工具类
 * 包装一层 cn.hutool.crypto.digest.BCrypt
 * @author wmgx
 * @create 2021-01-30-22:33
 **/
public class PasswordEncryptionUtils {
    /**
     * 将密码加密
     * 包装一层BCrypt 生成自定义长度的盐
     * @param password
     * @return
     */
    public static String hashPassword(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt(15));
    }
    /**
     * 验证密码是否一致
     * 包装一层BCrypt 保持一致性
     * @param password
     * @return
     */
    public static Boolean checkPassword(String password,String hashed){
        return BCrypt.checkpw(password,hashed);
    }
}
