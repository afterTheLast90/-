package com.hanhai.cloud.exception;

import com.hanhai.cloud.base.BaseException;
import com.hanhai.cloud.constant.ResultCode;

/**
 * @author wmgx
 * @create 2021-06-09-12:54
 **/
public class SmsException extends BaseException {
    public SmsException(){
        super(ResultCode.ACTION_ERROR);
    }
    public SmsException(String msg){
        super(ResultCode.ACTION_ERROR);
        this.setMsg(msg);
    }
}
