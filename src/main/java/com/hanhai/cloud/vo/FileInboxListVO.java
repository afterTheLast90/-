package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class FileInboxListVO {
    /** 收件箱id */
    private Long inboxId ;
    /** 标题 */
    private String title ;
    /** 提交人数 */
    private Integer commitCount ;
    /** 创建时间 */
    private LocalDateTime createdTime ;
    /** 截止时间 */
    private LocalDateTime endTime ;
    /** 是否提交截止 */
    public boolean getIsEnd(){
        return LocalDateTime.now().isAfter(endTime);
    }
}
