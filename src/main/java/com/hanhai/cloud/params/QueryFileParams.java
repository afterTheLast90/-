package com.hanhai.cloud.params;

import com.hanhai.cloud.base.PageParam;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class QueryFileParams extends PageParam {
    /** 文件父目录 */
    @NotNull(message = "查找目录不能为空")
    private String fileParentPath;
    /** 文件名 */
    private String fileName;
}
