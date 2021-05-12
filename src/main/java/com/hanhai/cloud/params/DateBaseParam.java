package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wmgx
 * @create 2021-05-05-19:46
 **/
@Data
@Accessors(chain = true)
public class DateBaseParam {
    @NotBlank(message = "数据库地址不能为空")
    @NotNull(message = "数据库地址不能为空")
    private String dbAddress;

    @NotBlank(message = "数据库名称不能为空")
    @NotNull(message = "数据库名称不能为空")
    private String dbName;

    @Max(value = 65535 ,message = "数据库端口错误")
    @Min(value = 1 ,message = "数据库端口错误")
    @NotNull(message = "数据库端口不能为空")
    private Integer dbPort;

    @NotBlank(message = "数据库用户名不能为空")
    @NotNull(message = "数据库用户名不能为空")
    private String dbUserName;

    @NotBlank(message = "数据库密码不能为空")
    @NotNull(message = "数据库密码不能为空")
    private String dbPassword;


    public String getJDBCUrl(){
        return "jdbc:mysql://"+getDbAddress()+":"+getDbPort()+"/"+getDbName() +
                "?serverTimezone=UTC";
    }
}
