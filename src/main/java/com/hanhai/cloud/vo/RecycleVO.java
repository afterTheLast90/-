package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class RecycleVO {
    /**回收站id**/
    private Long recycleId;
    /**文件名**/
    private String fileName;
    /**文件类型**/
    private String fileType;
    /**创建时间**/
    private LocalDateTime createdTime;
    /**删除标记**/
    private Boolean deleted;
    /***更新时间*/
    private LocalDateTime updatedTime;

    private Long expireDate;

    public Long getExpireDate() {
        return  Duration.between(LocalDateTime.now(),createdTime.plusDays(30)).toDays();
    }
}
