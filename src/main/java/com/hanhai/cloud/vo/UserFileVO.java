package com.hanhai.cloud.vo;


import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class UserFileVO {
    /**
     * 用户文件id
     */
    private Long userFileId ;
    /** 文件名 */
    private String fileName ;
    /** 文件大小 */
    private Long fileSize ;
    /** 父目录的全路径;存id譬如1/2/3/4 */
    private String fileParentPath ;
    /** 文件类型 */
    private String fileType ;
    /** 共享次数 */
    private Integer shareCount ;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
