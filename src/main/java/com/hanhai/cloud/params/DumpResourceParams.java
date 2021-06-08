package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class DumpResourceParams {
    /** 用户文件id */
    @NotNull(message = "请选择要保存的文件")
    @NotEmpty(message = "请选择要保存的文件")
    private Long[] userFileIds;
    /** 保存目标路径 */
    @NotNull(message = "请选择保存的路径")
    private String targetPath;
    /** 分享id（转存后，转存次数需要+1） */
    private String[] shareIds;
}
