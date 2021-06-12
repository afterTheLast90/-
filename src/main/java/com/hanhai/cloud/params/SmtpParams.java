package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;

/**
 * @author wmgx
 * @create 2021-06-08-20:22
 **/
@Data
@Accessors(chain = true)
public class SmtpParams {

    @NotNull(message = "服务器地址不能为空")
    @NotBlank(message = "服务器地址不能为空")
    private String smtpServer;

    @NotNull(message = "服务器端口号不能为空")
    @Min(value = 1,message = "端口号范围错误")
    @Max(value = 65535,message = "端口号范围错误")
    private Integer smtpPort;

    @Email(message = "邮件发送地址错误")
    @NotNull(message = "邮件发送地址不能为空")
    @NotBlank(message = "邮件发送地址不能为空")
    private String smtpUsername;

    @NotNull(message = "邮件发送密码不能为空")
    @NotBlank(message = "邮件发送密码不能为空")
    private String smtpPassword;

    @NotNull(message = "邮件发送者名称不能为空")
    @NotBlank(message = "邮件发送者名称不能为空")
    private String smtpSender;
}
