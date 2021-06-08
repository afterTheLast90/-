package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddTagRelationParams {
    /** 用户文件id */
    private Long userFileId;
    /** 标签id */
    private Long tagId;
}
