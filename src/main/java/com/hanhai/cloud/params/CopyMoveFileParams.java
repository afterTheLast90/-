package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CopyMoveFileParams {

    private Long [] ids;
    private String target;
}
