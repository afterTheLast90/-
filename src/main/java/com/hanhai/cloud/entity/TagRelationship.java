package com.hanhai.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hanhai.cloud.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wmgx
 * @create 2021-04-27-16:16
 **/

@Data
@Accessors(chain = true)
@TableName("tag_relationship")
public class TagRelationship extends BaseEntity implements Serializable,Cloneable {
    @TableId
    /** 标签对应关系 */
    private Long tagRelationshipId ;
    /** 用户文件Id */
    private Long userFileId ;
    /** 标签id */
    private Long tagId ;
}
