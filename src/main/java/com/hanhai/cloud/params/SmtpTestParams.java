package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wmgx
 * @create 2021-06-08-20:25
 **/
@Data
@Accessors(chain = true)
public class SmtpTestParams extends SmtpParams{
    @Email(message = "测试邮件接收地址错误")
    @NotNull(message = "接收测试邮件地址不能为空")
    @NotBlank(message = "接收测试邮件地址不能为空")
    private String testRec;
}
