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

//    /** 内容 */
//    private String content ;
//    /** 输入提示 */
//    private String inputTips ;
    /** 提交人数 */
    private Integer commitCount ;
//    /** 保存路径 */
//    private String savePath ;
    /** 截至时间 */
    private LocalDateTime endTime ;
    /** 截至时间 */
    private LocalDateTime createdTime ;

}
