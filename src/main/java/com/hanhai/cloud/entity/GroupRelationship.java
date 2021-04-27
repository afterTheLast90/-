package com.hanhai.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hanhai.cloud.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wmgx
 * @create 2021-04-27-16:18
 **/

@Data
@Accessors(chain = true)
@TableName("group_relationship")
public class GroupRelationship extends BaseEntity implements Serializable,Cloneable{

    @TableId
    /** 组关系id */
    private Long groupRelationshipId ;
    /** 组id */
    private Long groupId ;
    /** 用户id */
    private Long userId ;
}
