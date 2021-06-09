package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class DeletedParams {
    @NotNull(message = "ID不能为空")
    private Long [] ids;
}
