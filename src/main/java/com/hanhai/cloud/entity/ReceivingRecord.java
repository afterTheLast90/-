package com.hanhai.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hanhai.cloud.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wmgx
 * @create 2021-04-27-16:13
 **/

@Data
@Accessors(chain = true)
@TableName("receiving_record")
public class ReceivingRecord extends BaseEntity  implements Serializable,Cloneable{
    /** 收件ID */
    @TableId
    private Long receivingId ;
    /** 用户id */
    private Long userId ;
    /** 文件id */
    private Long fileId ;
    /** 用户文件id */
    private Long userFileId ;
    /** 收集id */
    private Long inboxId ;
    /** 是否被覆盖 */
    private String over ;
    /** 输入名 */
    private String inputName ;
    /** 提交原文件名 */
    private String commitFileName ;
}
