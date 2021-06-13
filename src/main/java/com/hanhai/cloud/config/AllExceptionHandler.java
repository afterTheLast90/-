package com.hanhai.cloud.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.hanhai.cloud.base.BaseException;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.configuration.SystemInfo;
import com.hanhai.cloud.constant.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-05-03-8:25
 **/
@ControllerAdvice
public class AllExceptionHandler {

    @Autowired
    SystemInfo systemInfo;
    // 最高级的异常，拦截所有
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R exceptionHandler(Exception e) {
        e.printStackTrace();
        return new R(400, e.getMessage(), null);
    }


    //校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public R bindExceptionHandler(MethodArgumentNotValidException e) {
        BindingResult exceptions = e.getBindingResult();
        // 判断异常中是否有错误信息，如果存在就使用异常中的消息，否则使用默认消息
        if (exceptions.hasErrors()) {
            List<ObjectError> errors = exceptions.getAllErrors();
            if (!errors.isEmpty()) {
                // 这里列出了全部错误参数，按正常逻辑，只需要第一条错误即可
                FieldError fieldError = (FieldError) errors.get(0);
                return new R(ResultCode.PARAMETER_ERROR).setMsg(fieldError.getDefaultMessage());

            }
        }
        return new R(ResultCode.PARAMETER_ERROR);
    }
        // 自定义异常
    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public R exceptionMineHandler(BaseException e) {
        return new R(e.getCode(), e.getMsg(), null);
    }

    // 未登录异常
    @ExceptionHandler(NotLoginException.class) //NotLoginException
    public String notLoginExceptionHandler(NotLoginException e){
        if (systemInfo.getInstalled())
            return "redirect:login";
        else
            return "redirect:install";
    }
//
    // 未登录异常
    @ExceptionHandler(NotPermissionException.class) //NotLoginException
    public String notPermissionExceptionHandler(NotPermissionException e){
        return "redirect:main";
    }
}

