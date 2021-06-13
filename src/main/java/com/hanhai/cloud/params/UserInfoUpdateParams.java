package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class UserInfoUpdateParams {
    /** 用户名 */
    @NotBlank(message="用户名不能为空")
    @NotNull(message = "用户名不能为空")
    private String userName ;
//    /** 头像 */
//    @NotBlank(message="头像不能为空")
//    @NotNull(message = "头像不能为空")
//    private String userAvatar ;
    /** 性别;0男1女 */
    @NotNull(message = "性别不能为空")
    private Integer userGender ;
//    /** 电子邮箱 */
//    @NotBlank(message = "电子邮箱不能为空")
//    @Email
//    @NotNull(message = "电子邮箱不能为空")
//    private String userEmail ;
//    /** 手机号 */
//    @NotBlank(message = "手机号不能为空")
//    @Pattern(regexp = "^1[345678]\\d{9}$", message = "手机号格式错误")
//    @NotNull(message = "手机号不能为空")
//    private String userPhone ;
}
