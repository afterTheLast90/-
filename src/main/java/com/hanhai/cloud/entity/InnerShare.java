package com.hanhai.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hanhai.cloud.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wmgx
 * @create 2021-04-27-16:11
 **/
@Data
@Accessors(chain = true)
@TableName("inner_share")
public class InnerShare extends BaseEntity implements Serializable,Cloneable{

    /** 内部分享id */
    @TableId
    private Long innerShareId ;
    /** 分享id */
    private Long shareId ;
    /** 用户Id */
    private Long userId ;
    /** 组id */
    private Long groupId ;
}
