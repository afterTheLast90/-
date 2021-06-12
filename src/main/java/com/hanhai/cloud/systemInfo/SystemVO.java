package com.hanhai.cloud.systemInfo;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author wmgx
 * @create 2021-05-19-23:54
 **/
@Data
public class SystemVO {
    /**
     * CPU相关信息
     */
    private Cpu cpu = new Cpu();

    /**
     * 內存相关信息
     */
    private Mem mem = new Mem();

    /**
     * JVM相关信息
     */
    private Jvm jvm = new Jvm();

    /**
     * 服务器相关信息
     */
    private Sys sys = new Sys();

    /**
     * 磁盘相关信息
     */
    private List<SysFile> sysFiles = new LinkedList<SysFile>();

    private Net net;

}
