package com.hanhai.cloud.params;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 修改收件箱任务参数
 * author 郝天乐
 */
@Data
public class FileInboxUpdateParams {
    /** 收件箱id */
    @NotNull(message = "收件箱ID不能为空！")
    private Long inboxId ;

    /** 标题 */
    @NotNull(message = "标题不能为空！")
    @NotBlank(message = "标题不能为空！")
    private String title ;

    /** 输入提示 */
    @NotNull(message = "输入提示不能为空！")
    @NotBlank(message = "输入提示不能为空！")
    private String inputTips ;

    /** 用户类型 */
    @NotNull(message = "用户权限不能为空！")
    private int commitType;

    /** 截至时间 */
    @NotNull(message = "截止时间不能为空！")
    @NotBlank(message = "截止时间不能为空！")
    private String endTime ;

    /** 收集内容 */
    @NotNull(message = "收集内容不能为空！")
    @NotBlank(message = "收集内容不能为空！")
    private String content ;
}
