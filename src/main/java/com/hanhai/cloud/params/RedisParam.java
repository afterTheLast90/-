package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wmgx
 * @create 2021-05-27-21:57
 **/
@Data
@Accessors(chain = true)
public class RedisParam {
    private String redisPassword;
    @NotNull(message = "数据库地址不能为空")
    @NotBlank(message = "数据库地址不能为空")
    private String redisAddress;
    @NotNull(message = "数据库端口不能为空")
    @Min(value = 1 ,message = "数据库端口范围错误")
    @Max(value = 65535 ,message = "数据库端口范围错误")
    private Integer redisPort;

}
