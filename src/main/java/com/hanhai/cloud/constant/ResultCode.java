package com.hanhai.cloud.constant;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wmgx
 * @create 2021-05-03-8:09
 **/
public enum ResultCode {


    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),


    /**
     * 操作成功 前端不显示
     */
    SUCCESS_NO_SHOW(201, "操作成功"),

    /**
     * 更新成功
     */
    UPDATE_SUCCESS(200,"更新成功") ,

    /**
     * 更新失败
     */
    UPDATE_ERROR(453,"更新失败") ,

    /**
     * 参数错误
     */
    PARAMETER_ERROR(451,"参数错误"),


    /**
     * 登录失败
     */
    LOGIN_ERROR(452,"用户名或密码错误"),

    /**
     * 验证码错误
     */
    CAPTCHA_ERROR(453,"验证码错误"),


    /**
     * 非法访问
     */
    UNAUTHORIZED_ACCESS(490,"非法访问");
    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Integer code;

    private String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
