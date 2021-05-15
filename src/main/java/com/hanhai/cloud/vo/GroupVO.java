package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GroupVO {
    /** 组id */
    private Long groupId ;
    /** 组名 */
    private String groupName ;
    /** 人数 */
    private Integer numberOfPersones ;
}
