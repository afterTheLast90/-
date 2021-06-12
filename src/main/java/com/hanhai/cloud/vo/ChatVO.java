package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-05-18-9:05
 **/
@Data
@Accessors(chain = true)
public class ChatVO {
    private List<String> xAxisData;
    private List<Object> seriesData;
}
