package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wmgx
 * @create 2021-06-09-22:50
 **/
@Data
@Accessors(chain = true)
public class RePasswordParams {
    @NotNull(message = "账户不能为空")
    @NotBlank(message = "账户不能为空")
    private String account;
    @NotNull(message = "新密码不能为空")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
    @NotNull(message = "验证码不能为空")
    @NotBlank(message = "验证码不能为空")
    private String captcha;
}
