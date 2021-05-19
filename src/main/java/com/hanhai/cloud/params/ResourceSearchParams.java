package com.hanhai.cloud.params;

import com.hanhai.cloud.base.PageParam;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@Accessors(chain = true)
public class ResourceSearchParams extends PageParam {
    /** 文件名 */
    @Value("")
    private String fileName;
}
