package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class HistoryVO {
    /** 历史id */
    private Long historyId ;
    /** 文件id */
    private Long fileId ;
    /** 用户文件id */
    private Long userFileId ;
    /** 历史文件名 */
    private String  fileName ;
    /** 历史文件大小 */
    private Long fileSize ;
    /** 修改人;0为匿名用户 */
    private String updatePersonName ;
    /**创建时间**/
    private LocalDateTime createdTime;
    /**更新时间**/
    private LocalDateTime updatedTime;
}
