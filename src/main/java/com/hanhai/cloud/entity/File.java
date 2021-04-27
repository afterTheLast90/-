package com.hanhai.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hanhai.cloud.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wmgx
 * @create 2021-04-25-10:39
 **/
@TableName("files")
@Data
@Accessors(chain = true)
public class File extends BaseEntity implements Serializable,Cloneable {

    @TableId
    /** 文件ID */
    private Long fileId ;
    /** 文件md5 */
    private String fileMd5 ;
    /** 文件路径 */
    private String filePath ;
    /** 文件大小(字节为单位) */
    private Long fileSize ;
    /** 存储位置;0本地磁盘 */
    private Integer storageLocation ;
    /** 引用次数 */
    private Long citationsCount ;
}
