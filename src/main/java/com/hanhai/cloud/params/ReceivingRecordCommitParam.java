package com.hanhai.cloud.params;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 收件记录提交参数
 * author 郝天乐
 */
@Data
public class ReceivingRecordCommitParam {
//    /** 用户id */
//    @NotNull(message = "用户ID不能为空！")
//    private Long userId ;

    /** 文件id */
    @NotNull(message = "文件ID不能为空！")
    private Long fileId ;

    /** 用户文件id */
//    @NotNull(message = "用户文件ID不能为空！")
    private Long userFileId ;

    /** 收集id */
    @NotNull(message = "收集ID不能为空！")
    private Long inboxId ;

    /** 输入名 */
    @NotNull(message = "输入名不能为空！")
    @NotBlank(message = "输入名不能为空！")
    private String inputName ;

    /** 提交原文件名 */
    @NotNull(message = "原文件名不能为空！")
    @NotBlank(message = "原文件名不能为空！")
    private String commitFileName ;
}
