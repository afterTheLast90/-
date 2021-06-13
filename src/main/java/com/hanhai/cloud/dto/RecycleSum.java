package com.hanhai.cloud.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wmgx
 * @create 2021-06-12-23:50
 **/
@Data
@Accessors(chain = true)
public class RecycleSum {
    private Long fileId;
    private Long userId;
    private Long sum;
    private Long cou;
}
