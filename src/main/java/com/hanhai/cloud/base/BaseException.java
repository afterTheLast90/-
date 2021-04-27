package com.hanhai.cloud.base;


/**
 * @author wmgx
 * @create 2021-02-01-19:21
 **/
public class BaseException extends Exception{
    private Integer code;
    private String msg;

    public BaseException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseException() {
    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
