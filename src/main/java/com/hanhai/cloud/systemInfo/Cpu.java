package com.hanhai.cloud.systemInfo;

import cn.hutool.core.util.NumberUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wmgx
 * @create 2021-05-19-8:30
 **/
@Data
public class Cpu implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 核心数
     */
    private Integer cpuNum;

    /**
     * CPU总的使用率
     */
    private Double total;

    /**
     * CPU系统使用率
     */
    private Double sys;

    /**
     * CPU用户使用率
     */
    private Double used;

    /**
     * CPU当前等待率
     */
    private Double wait;

    /**
     * CPU当前空闲率
     */
    private Double free;


    public Double getTotal() {
        return NumberUtil.round(NumberUtil.mul(total.doubleValue(), 100), 2).doubleValue();
    }

    public Double getSys() {
        return NumberUtil.round(NumberUtil.mul(sys / total, 100), 2).doubleValue();
    }

    public Double getUsed() {
        return NumberUtil.round(NumberUtil.mul(used / total, 100), 2).doubleValue();
    }

    public Double getWait() {
        return NumberUtil.round(NumberUtil.mul(wait / total, 100), 2).doubleValue();
    }

    public Double getFree() {
        return NumberUtil.round(NumberUtil.mul(free / total, 100), 2).doubleValue();
    }
}




