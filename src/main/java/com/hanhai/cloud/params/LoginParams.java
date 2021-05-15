package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wmgx
 * @create 2021-05-03-8:16
 **/
@Data
@Accessors(chain = true)
public class LoginParams {
    @NotNull(message = "用户名不能为空")
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotNull(message = "密码不能为空")
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotNull(message = "验证码不能为空")
    @NotBlank(message = "验证码不能为空")
    private String captcha;
//    private Boolean rememberMe=false;
}
