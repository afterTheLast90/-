package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class GroupAddUserParam {
    /** 组id */
    @NotNull(message = "组id为空")
    private Long groupId;
    /** 用户id组 */
    @NotEmpty(message = "用户列表为空")
    private Long[] userIds;
}
