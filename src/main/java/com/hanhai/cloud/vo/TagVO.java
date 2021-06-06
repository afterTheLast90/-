package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TagVO {
    /** 标签id */
    private Long tagId;
    /** 标签关系id */
    private Long tagRelationshipId;
    /** 标签名 */
    private String tagName;
    /** 文件数量 */
    private Integer fileCount;
}
