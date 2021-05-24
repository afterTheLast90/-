package com.hanhai.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hanhai.cloud.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wmgx
 * @create 2021-04-27-16:12
 **/
@Data
@Accessors(chain = true)
@TableName("file_inbox")
public class FileInbox extends BaseEntity implements Serializable,Cloneable {
    /** 收件箱id */
    @TableId
    private Long inboxId ;
    /** 标题 */
    private String title ;
    /** 发布人 */
    private Long publisher ;
    /** 内容 */
    private String content ;
    /** 输入提示 */
    private String inputTips ;
    /** 提交人数 */
    private Integer commitCount ;
    /** 保存路径 */
    private String savePath ;
    /** 截至时间 */
    private LocalDateTime endTime ;
    /** 用户类型 */
    private int commitType;
}
