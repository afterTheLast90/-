package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ReNameParams {
    private String newName;
    private Long [] ids;
    private String target;
}
