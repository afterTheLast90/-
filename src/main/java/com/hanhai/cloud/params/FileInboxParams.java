package com.hanhai.cloud.params;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * author 郝天乐
 */
@Data
public class FileInboxParams {
    /** 收件箱id */
    private Long inboxId ;
    /** 标题 */
    @NotNull(message = "标题不能为空！")
    @NotBlank()
    private String title ;
    /** 发布人 */
    private Long publisher ;
    /** 内容 */
    private String content ;
    /** 输入提示 */
    private String inputTips ;
    /** 提交人数 */
    private Integer commitCount ;
    /** 保存路径 */
    private String savePath ;
    /** 截至时间 */
    private String endTime ;
    /** 用户类型 */
    private int commitType;
}
