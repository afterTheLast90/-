package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ReceivingRecordListVO {
    /** 收件ID */
    private Long receivingId ;
    /** 文件id */
    private Long fileId ;
    /** 用户文件id */
    private Long userFileId ;
    /** 收集id */
    private Long inboxId ;
    /** 输入名 */
    private String inputName ;
    /** 提交原文件名 */
    private String commitFileName ;
    /** 提交时间 */
    private LocalDateTime createdTime ;
}
