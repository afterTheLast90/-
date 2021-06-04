package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddInnerShare extends CreateShareParams{
    /** 私有分享 组列表 */
    private Long[] groupIds;
    /** 私有分享 用户列表 */
    private Long[] userIds;

}
