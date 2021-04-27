package com.hanhai.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hanhai.cloud.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wmgx
 * @create 2021-04-27-16:15
 **/

@Data
@Accessors(chain = true)
@TableName("tags")
public class Tag extends BaseEntity implements Serializable,Cloneable {
    @TableId
    /** 标签id */
    private Long tagId ;
    /** 标签名 */
    private String tagName ;
    /** 标签所属者;0代表公有 */
    private Long tagOwner ;
    /** 文件数量 */
    private Integer fileCount ;
}
