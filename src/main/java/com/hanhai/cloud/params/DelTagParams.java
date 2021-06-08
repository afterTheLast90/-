package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Validated
@Accessors(chain = true)
public class DelTagParams {
    /** 标签关系id */
//    @NotNull(message = "标签关系id不能为空")
    @Min(value = 1, message = "id非法")
    private Long relationId;
    /** 标签关系ids */
    private Long[] tagRelationsId;
    /** 标签id */
    @NotNull(message = "标签Id不能为空")
    @Min(value = 1, message = "id非法")
    private Long tagId;
}


