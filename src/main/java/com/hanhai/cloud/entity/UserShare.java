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
 * @create 2021-04-25-10:44
 **/

@Data
@Accessors(chain = true)
@TableName("user_share")
public class UserShare extends BaseEntity implements Serializable,Cloneable{
    /** 分享id */
    @TableId
    private String shareId ;
    /** 用户id */
    private Long userId ;
    /** 文件id */
    private Long userFileId ;
    /** 分享类型;0-公有分享
     1-企业内分享（登录后可以查看）
     2-私有分享（指定人查看）
     3-密码查看
     4-仅通过访问码查看 */
    private Integer shareType ;
    /** 最大下载次数 */
    private Integer maxDownloadTimes ;
    /** 最大转存次数 */
    private Integer maxFileDumpTimes ;
    /** 过期时间;-1为永不过期即 1970-01-01T07:59:59 */
    private LocalDateTime expireTime ;
    /** 下载次数 */
    private Integer downloadTimes ;
    /** 转存次数 */
    private Integer fileDumpTime ;
    /** 密码 */
    private String sharePassword ;
}
