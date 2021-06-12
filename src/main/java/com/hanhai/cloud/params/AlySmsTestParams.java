package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author wmgx
 * @create 2021-06-09-14:20
 **/
@Data
@Accessors(chain = true)
public class AlySmsTestParams extends AlySmsParams{

    @NotBlank(message = "接收信息的手机号不能为空")
    @NotNull(message = "接收信息的手机号不能为空")
    @Pattern(regexp = "^1[345678]\\d{9}$", message = "手机号格式错误")
    private String testPhoneRec;
}
