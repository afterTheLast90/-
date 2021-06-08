package com.hanhai.cloud.params;

import com.hanhai.cloud.base.PageParam;
import com.hanhai.cloud.vo.ResourceVO;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class ResourceSearchParams extends PageParam {
    /** 文件名 */
//    @NotNull(message = "文件名不能为空")
    @Value("")
    private String fileName;
    /** 分享id */
    private String shareId;
    /** 用户文件id */
    private Long userFileId;
    /** 当前路径 */
    private String currentPath;
    /** 是否返回上一级 */
    private Boolean goUp;
}
