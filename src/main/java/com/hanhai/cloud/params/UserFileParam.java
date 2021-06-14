package com.hanhai.cloud.params;

import lombok.Data;

/**
 * 郝天乐
 */
@Data
public class UserFileParam {
    /** 父目录全路径 */
    private String path;
    /** 文件名 */
    private String name;
    private String[] names;
    /** 输入名 */
    private String inputName;
    /** 用户ID */
    private String userId;
}
