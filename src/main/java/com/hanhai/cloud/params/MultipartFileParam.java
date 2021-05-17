package com.hanhai.cloud.params;

import lombok.Data;

/**
 * 郝天乐
 */
@Data
public class MultipartFileParam {
    /** md5值 */
    private String md5;
    /** 文件id */
    private String id;
    /** 文件名 */
    private String name;
    /** 文件大小 */
    private String size;
    /** 总片数 */
    private String total;
    /** 分片序号,当前第几片(从1开始) */
    private String index;
    /** 上传状态 check：检测；upload：上传 */
    private String action;
    /** 分片md5值 */
    private String partMd5;
}
