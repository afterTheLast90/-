package com.hanhai.cloud.systemInfo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wmgx
 * @create 2021-05-27-23:11
 **/
@Data
@Accessors
public class Net {
    private Long allIn;
    private Long allOut;
    private Long in;
    private Long out;
}
