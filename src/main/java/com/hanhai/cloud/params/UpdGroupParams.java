package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
public class UpdGroupParams {

    @NotNull(message = "组id不能为空")
    private Long groupId;

    @NotNull(message = "组名不能为空")
    @NotBlank(message = "组名不能为空")
    private String groupName;
}
