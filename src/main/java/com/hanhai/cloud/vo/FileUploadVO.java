package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileUploadVO {
    /** 0:未上传,1:部分上传,2:已上传 */
    private String flag;
    /** 文件ID */
    private Long fileId;
    /** 保存分片 */
    private Integer chunk;
    /** 成功数 */
    private Integer succeed;
    /** MD5值 */
    private String md5;
    /** 用户文件ID */
    private Long userFileId;
}
