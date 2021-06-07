package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;

/**
 * @author wmgx
 * @create 2021-06-05-22:12
 **/
@Data
@Accessors(chain = true)
public class UserInfoParams {
    /** 用户名 */
    @NotNull(message = "用户名不能为空")
    @NotBlank(message = "用户名不能为空")
    private String userName ;
    /** 性别;0男1女 */
    @NotNull(message = "性别不能为空")
    @Min(value = 0,message = "性别错误")
    @Max(value = 1,message = "性别错误")
    private Integer userGender ;
    /** 电子邮箱 */
    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    private String userEmail ;

    /** 密码 */
    private String userPassword ;
    /** 手机号 */
    @Pattern(regexp = "^1[345678]\\d{9}$", message = "手机号格式错误")
    @NotBlank(message = "手机号不能为空")
    @NotNull(message = "手机号不能为空")
    private String userPhone ;
    /** 电子邮箱是否验证通过 */
    @NotNull(message = "邮箱是否需要验证不能为空")
    private Boolean emailChecked=false ;
    /** 手机号是否验证通过 */
    @NotNull(message = "手机号是否需要验证不能为空")
    private Boolean phoneChecked =false;
    /** 空间大小（字节为单位） */
    @NotNull(message = "默认空间大小不能为空")
    private Long spaceSize ;
    /** 是否具有管理权限 */
    @NotNull(message = "管理权限不能为空")
    private Boolean admin =false;

}
