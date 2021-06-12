package com.hanhai.cloud.systemInfo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wmgx
 * @create 2021-05-19-8:30
 **/
@Data
public class Mem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内存总量
     */
    private Long total;

    /**
     * 已用内存
     */
    private Long used;

    /**
     * 剩余内存
     */
    private Long free;

//    public Double getTotal() {
//        return NumberUtil.div(total.doubleValue(), (1024 * 1024 * 1024), 2);
//    }
//
//    public Double getUsed() {
//        return NumberUtil.div(used.doubleValue(), (1024 * 1024 * 1024), 2);
//    }
//
//
//    public Double getFree() {
//        return NumberUtil.div(free.doubleValue(), (1024 * 1024 * 1024), 2);
//    }

//    public Double getUsage() {
//        return NumberUtil.mul(NumberUtil.div(used, total, 4), 100);
//    }
}