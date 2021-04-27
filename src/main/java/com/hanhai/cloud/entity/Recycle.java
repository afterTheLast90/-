package com.hanhai.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hanhai.cloud.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wmgx
 * @create 2021-04-25-10:44
 **/
@TableName("recycle")
@Data
@Accessors(chain = true)
public class Recycle extends BaseEntity implements Serializable,Cloneable{
   @TableId
    /** 回收站id */
    private Long recycleId ;
    /** 文件名 */
    private String fileName ;
    /** 文件类型 */
    private String fileType ;
}
