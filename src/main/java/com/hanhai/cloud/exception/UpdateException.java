package com.hanhai.cloud.exception;

import com.hanhai.cloud.base.BaseException;
import com.hanhai.cloud.constant.ResultCode;

public class UpdateException extends BaseException {
    public UpdateException(){
        super(ResultCode.UPDATE_ERROR);
    }
}
