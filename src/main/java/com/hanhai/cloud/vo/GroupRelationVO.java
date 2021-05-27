package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;

@Data
@Accessors(chain = true)
public class GroupRelationVO {
    /** 组关系id */
    @Value("")
    private Long groupRelationshipId;
    /** 组id */
    private Long groupId;
    /** 用户名 */
    private String userName;
    /** 用户头像 */
    private String userAvatar;
}
