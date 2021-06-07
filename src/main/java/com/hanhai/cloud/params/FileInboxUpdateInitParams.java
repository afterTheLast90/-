package com.hanhai.cloud.params;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 修改收件箱初始化参数
 * author 郝天乐
 */
@Data
public class FileInboxUpdateInitParams {
    /** 收件箱id */
    @NotNull(message = "收件箱ID不能为空！")
    private Long inboxId ;
}
