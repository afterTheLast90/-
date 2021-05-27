package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ShareMumbersVO {
    /** 组列表 */
    private List<String> groupName;
    /** 用户列表 */
    private List<String> userName;
}
