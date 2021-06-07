package com.hanhai.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hanhai.cloud.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wmgx
 * @create 2021-04-25-10:46
 **/
@TableName("file_history")
@Data
@Accessors(chain = true)
public class FileHistory extends BaseEntity implements Serializable,Cloneable {
    @TableId
    /** 历史id */
    private Long historyId ;
    /** 文件id */
    private String fileId ;
    /** 用户文件id */
    private String userFileId ;
    /** 历史文件名 */
    private String fileName ;
    /** 历史文件大小 */
    private String fileSize ;
    /** 修改人;0为匿名用户 */
    private Long updatePerson ;
}
