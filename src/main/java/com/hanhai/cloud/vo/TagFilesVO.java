package com.hanhai.cloud.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.hanhai.cloud.base.PageParam;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class TagFilesVO{
    /** 用户文件id */
    private Long userFileId;
    /** 关系id */
    private Long tagRelationshipId ;
    /** 文件名 */
    private String fileName ;
    /** 文件类型*/
    private String fileType;
    /** 文件大小 */
    private Long fileSize ;
    /** 修改时间 */
    private LocalDateTime updatedTime;
}
