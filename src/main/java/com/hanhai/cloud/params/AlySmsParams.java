package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wmgx
 * @create 2021-06-09-14:18
 **/
@Data
@Accessors(chain = true)
public class AlySmsParams {
    @NotBlank(message = "阿里云短信服务的地区id不能为空")
    @NotNull(message = "阿里云短信服务的地区id不能为空")
    private String alySmsRegionId;
    @NotBlank(message = "阿里云服务访问key不能为空")
    @NotNull(message = "阿里云服务访问key不能为空")
    private String alySmsAccessKeyId;
    @NotBlank(message = "阿里云服务访问密码不能为空")
    @NotNull(message = "阿里云服务访问密码不能为空")
    private String alySmsSecret;
    @NotBlank(message = "阿里云短息服务模板id不能为空")
    @NotNull(message = "阿里云短息服务模板id不能为空")
    private String alyTemplateCode;
}
