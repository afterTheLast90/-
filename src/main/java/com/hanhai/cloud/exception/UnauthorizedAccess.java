package com.hanhai.cloud.exception;

import com.hanhai.cloud.base.BaseException;
import com.hanhai.cloud.constant.ResultCode;

public class UnauthorizedAccess extends BaseException {
    public UnauthorizedAccess() {
        super(ResultCode.UNAUTHORIZED_ACCESS);
    }
}
