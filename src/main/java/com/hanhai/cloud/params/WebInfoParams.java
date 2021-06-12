package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author wmgx
 * @create 2021-06-09-16:14
 **/
@Data
@Accessors(chain = true)
public class WebInfoParams {
    @NotNull(message = "网站名称不能为空")
    @NotBlank(message = "网站名称不能为空")
    private String siteName;

    @Pattern(regexp = "(http|https):\\/\\/([\\w.]+\\/?)\\S*", message = "URL必须以http或者https开头")
    @NotNull(message = "网站URL不能为空")
    @NotBlank(message = "网站URL不能为空")
    private String siteUrl;

    @NotNull(message = "网站备案号不能为空")
    @NotBlank(message = "网站备案号不能为空")
    private String siteIcp;

    @NotNull(message = "用户默认空间大小不能为空")
    @Min(value = 0,message = "空间大小非法")
    private Long defaultSpaceSize;
}
