package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wmgx
 * @create 2021-06-11-12:03
 **/
@Data
@Accessors(chain = true)
public class RegisterParams {
    /** 用户名 */
    @NotNull(message = "用户名不能为空")
    @NotBlank(message = "用户名不能为空")
    private String userName ;
    /** 密码 */
    @NotNull(message = "密码不能为空")
    @NotBlank(message = "密码不能为空")
    private String userPassword ;
    /** 性别;0男1女 */
    @NotNull(message = "性别不能为空")
    @Min(value = 0,message = "性别非法")
    @Max(value = 1,message = "性别非法")
    private Integer userGender ;
    /** 电子邮箱 */
    @NotNull(message = "电子邮箱不能为空")
    @NotBlank(message = "电子邮箱不能为空")
    private String userEmail ;
    /** 手机号 */
    @NotNull(message = "手机号不能为空")
    @NotBlank(message = "手机号不能为空")
    private String userPhone ;
    /** 手机验证码*/
    @NotNull(message = "手机验证码不能为空")
    @NotBlank(message = "手机验证码不能为空")
    private String registerPhoneVerificationCode;
    /** 邮箱验证码 */
    @NotNull(message = "邮箱验证码不能为空")
    @NotBlank(message = "邮箱验证码不能为空")
    private String registerEmailVerificationCode;

}
