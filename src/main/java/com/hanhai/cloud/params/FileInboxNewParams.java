package com.hanhai.cloud.params;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 新建收件箱任务参数
 * author 郝天乐
 */
@Data
public class FileInboxNewParams {
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

    /** 保存路径 */
    @NotNull(message = "保存路径不能为空！")
    @NotBlank(message = "保存路径不能为空！")
    private String savePath ;

    /** 截至时间 */
    @NotNull(message = "截止时间不能为空！")
    @NotBlank(message = "截止时间不能为空！")
    private String endTime ;

    /** 收集内容 */
    @NotNull(message = "收集内容不能为空！")
    @NotBlank(message = "收集内容不能为空！")
    private String content ;

}
