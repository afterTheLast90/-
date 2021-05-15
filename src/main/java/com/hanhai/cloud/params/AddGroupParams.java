package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
public class AddGroupParams  {

    @NotBlank(message = "组名不能为空")
    @NotNull(message = "组名不能为空")
    private String groupName;

    @NotNull(message = "用户列表不能为空")
    private List<Long> userList;
}
