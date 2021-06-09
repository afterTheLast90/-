package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ReducingParams {
    private Long id;
    private String target;
}
