package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.bind.annotation.DeleteMapping;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class UpdTagParams {
    @NotNull(message = "标签id不能为空")
    private Long tagId;

    @NotNull(message = "标签名不能为空")
    @NotBlank(message = "标签名不能为空")
    private String tagName;
}
