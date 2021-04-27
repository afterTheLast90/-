package com.hanhai.cloud.base;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回类
 * @author wmgx
 * @create 2021-01-29-19:19
 **/
@AllArgsConstructor
@Data
public class R<T> implements Serializable {
    public final static R SUCCESS = new R(200,"操作成功");

    public final static R UPDATE_SUCCESS = new R(200,"更新成功");
    private Integer code;
    private String msg;
    private T data;


    public R(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public Integer getCode() {
        return code;
    }

    public R<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public R<T>  setMsg(String msg) {
        this.msg = msg;

        return this;
    }

    public T getData() {
        return data;
    }

    public R<T>  setData(T data) {
        this.data = data;

        return this;
    }
    public String toString(){
        return  "{" +
                "\"code\"=" + code +","+
                "\"msg\"=" + "\""+msg+"\"" +
                '}';
    }
}
