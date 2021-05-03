package com.hanhai.cloud.base;

import com.hanhai.cloud.constant.ResultCode;
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
    public final static R SUCCESS = new R(ResultCode.SUCCESS);
    public final static R UPDATE_SUCCESS = new R(ResultCode.UPDATE_SUCCESS);
    private Integer code;
    private String msg;
    private T data;


    public R(ResultCode resultCode){
        this.code=resultCode.getCode();
        this.msg=resultCode.getMsg();
    }
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
