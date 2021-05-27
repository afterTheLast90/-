package com.hanhai.cloud.params;

import com.hanhai.cloud.base.PageParam;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class TagFilesParams extends PageParam {
    /** 标签id */
    private Long tagId ;
    /** 文件id */
    @Value("")
    @NotNull(message = "文件名不能为null")
    private String fileName;
}
