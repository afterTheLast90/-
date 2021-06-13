package com.hanhai.cloud.constant;

/**
 * @author wmgx
 * @create 2021-06-13-0:56
 **/

public final   class Pattern {

    public final static String [] publicPattern={
            // 静态资源
            "/static/**",
            // 获取头像接口
            "/avatar/*",
            // 验证码
            "/captcha",
            // 登录
            "/login",
            // 登录前验证邮箱发送邮件
            "/verified/send/email",
            // 登录前验证手机号发送短信
            "/verified/send/sms",
            // 验证验证码
            "/verified",
            // 注册发送短信
            "/register/send/sms",
            // 注册发送邮件
            "/register/send/email",
            // 注册
            "/register",
            // 主页
            "/",
            // 错误页面
            "/error"

    };

    public final static   String[] adminPattern={
            "/admin/**",
            "/userManager",
            "/dashBoard/*",
            "/admin",
            "/fileManager"
    };

    private Pattern(){

    }
}
