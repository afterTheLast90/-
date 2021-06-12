package com.hanhai.cloud.systemInfo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wmgx
 * @create 2021-05-19-8:32
 **/
@Data
public class SysFile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 盘符路径
     */
    private String dirName;

    /**
     * 盘符类型
     */
    private String sysTypeName;

    /**
     * 文件类型
     */
    private String typeName;

    /**
     * 总大小
     */
    private Long total;

    /**
     * 剩余大小
     */
    private Long free;

    /**
     * 已经使用量
     */
    private Long used;

    /**
     * 资源的使用率
     */
    private Double usage;
}