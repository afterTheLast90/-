package com.hanhai.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hanhai.cloud.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wmgx
 * @create 2021-04-27-16:17
 **/

@Data
@Accessors(chain = true)
@TableName("group")
public class Group extends BaseEntity implements Serializable,Cloneable{
    @TableId
    /** 组id */
    private Long groupId ;
    /** 组名 */
    private String groupName ;
    /** 人数 */
    private Integer numberOfPersones ;

}
