package com.hanhai.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hanhai.cloud.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @author wmgx
 * @create 2021-04-25-10:40
 **/

@Data
@Accessors(chain = true)
@TableName("user_files")
public class UserFile extends BaseEntity implements Serializable,Cloneable {
    /** 存储id */
    @TableId
    private Long userFileId ;
    /** 文件id */
    private Long fileId ;
    /** 文件名 */
    private String fileName ;
    /** 文件大小 */
    private Long fileSize ;
    /** 父目录的全路径;存id譬如1/2/3/4 */
    private String fileParentPath ;
    /** 文件类型 */
    private String fileType ;
    /** 回收站批次 */
    private Long recycleId ;
    /** 共享次数 */
    private Integer shareCount ;
}
