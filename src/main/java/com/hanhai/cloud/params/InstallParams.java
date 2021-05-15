package com.hanhai.cloud.params;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;


/**
 * @author wmgx
 * @create 2021-05-09-14:49
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class InstallParams extends DateBaseParam {

    /**
     * 网站名称
     */

    @NotBlank(message = "网站名称不能为空")
    @NotNull(message = "网站名称不能为空")
    private String siteName;
    /**
     * 网站url
     */

    @Pattern(regexp = "(http|https):\\/\\/([\\w.]+\\/?)\\S*", message = "URL必须以http或者https开头")
    @NotBlank(message = "网站url不能为空")
    @NotNull(message = "网站url不能为空")
    private String siteUrl;
    /**
     * 备案号
     */
    private String icp;
    /**
     * 管理员用户名
     */
    @NotBlank(message = "管理员名不能为空")
    @NotNull(message = "管理员名不能为空")
    private String userName;
    /**
     * 管理员邮箱
     */
    @Email(message = "邮箱格式错误")
    @NotBlank(message = "邮箱不能为空")
    @NotNull(message = "邮箱不能为空")
    private String email;
    /**
     * 管理员手机号
     */
    @Pattern(regexp = "^1[345678]\\d{9}$", message = "手机号格式错误")
    @NotBlank(message = "手机号不能为空")
    @NotNull(message = "手机号不能为空")
    private String phoneNumber;
    /**
     * 管理员密码
     */
    @NotBlank(message = "密码不能为空")
    @NotNull(message = "密码不能为空")
    private String password;
    /**
     * 上传文件保存路径
     */

    @NotBlank(message = "路径不能为空")
    @NotNull(message = "路径不能为空")
    private String fileUploadPath;
}