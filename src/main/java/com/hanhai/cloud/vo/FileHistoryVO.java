package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class FileHistoryVO {
    /**历史id**/
    private Long history_id;
    /**用户文件id**/
    private Long file_id;
    /**历史文件名**/
    private String file_name;
    /**历史文件大小**/
    private Long update_person;
    /**修改人**/
    private String file_size;
    /**删除标记**/
    private Boolean delete;
    /**创建时间**/
    private LocalDateTime create_time;
    /**更新时间**/
    private LocalDateTime updated_time;
}
