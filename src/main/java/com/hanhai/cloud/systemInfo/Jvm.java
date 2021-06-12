package com.hanhai.cloud.systemInfo;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.Data;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.Date;

/**
 * @author wmgx
 * @create 2021-05-19-8:31
 **/
@Data
public class Jvm implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前JVM占用的内存总数
     */
    private Long total;

    /**
     * JVM最大可用内存总数
     */
    private Long max;

    /**
     * JVM空闲内存
     */
    private Long free;

    /**
     * JDK版本
     */
    private String version;

    /**
     * JDK路径
     */
    private String home;

//    public Double getTotal() {
//        return NumberUtil.div(total.doubleValue(), (1024 * 1024), 2);
//    }
//
//    public Double getMax() {
//        return NumberUtil.div(max.doubleValue(), (1024 * 1024), 2);
//    }
//
//    public Double getFree() {
//        return NumberUtil.div(free.doubleValue(), (1024 * 1024), 2);
//    }

    public Long getUsed() {
        return total - free;
    }

    public String getVersion() {
        return version;
    }

    public String getHome() {
        return home;
    }

    public Double getUsage() {
        return NumberUtil.mul(NumberUtil.div(total - free, total.doubleValue(), 4), 100);
    }
    /**
     * 获取JDK名称
     */
    public String getName() {
        return ManagementFactory.getRuntimeMXBean().getVmName();
    }

    /**
     * JDK启动时间
     */
    public String getStartTime() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        Date date = new Date(time);
        return DateUtil.formatDateTime(date);
    }

    /**
     * JDK运行时间
     */
    public String getRunTime() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        Date date = new Date(time);

        //运行多少分钟
        long runMS = DateUtil.between(date, new Date(), DateUnit.MS);

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;

        long day = runMS / nd;
        long hour = runMS % nd / nh;
        long min = runMS % nd % nh / nm;
        return day + "天" + hour + "小时" + min + "分钟";
    }
}
