package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserFileFindNameVO {
    /** 用户文件id */
    private Long userFileId ;
    /** 文件ID */
    private Long fileId;
    /** 文件名 */
    private String fileName ;
    /** 文件大小 */
    private Long fileSize ;
}
