package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GroupAddUserParam {
    /** 组id */
    private Long groupId;
    /** 用户id组 */
    private Long[] userIds;
}
