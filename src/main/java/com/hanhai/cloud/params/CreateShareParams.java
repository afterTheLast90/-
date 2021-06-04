package com.hanhai.cloud.params;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CreateShareParams {
    /** 用户id */
//    private Long userId ;
    /** 文件id */
    @NotNull(message = "用户文件id不能为空")
    private Long userFileId ;
    /** 分享类型;0-公有分享
     1-企业内分享（登录后可以查看）
     2-私有分享（指定人查看）
     3-密码查看
     4-仅通过访问码查看 */
    @NotNull(message = "分享类型不能为空")
    private Integer shareType ;
    /** 最大下载次数 */
    private Integer maxDownloadTimes ;
    /** 最大转存次数 */
    private Integer maxFileDumpTimes ;
    /** 过期时间;-1为永不过期即 1970-01-01T07:59:59 */
    private LocalDateTime expireTime ;
    /** 密(取件）码 */
    private String sharePassword ;

    public CreateShareParams setExpireTime(Integer time){
        if(time == -1){
            expireTime = LocalDateTime.of(1970,1,1,7,59,59);
        }
        else{
            expireTime = LocalDateTime.now().plusDays(time);
        }
        return this;
    }

    public CreateShareParams setMaxFileDumpTimes(Integer times) {
        if (times == null || times == 0) {
            maxFileDumpTimes = -1;
        } else
            maxFileDumpTimes = times;
        return this;
    }

    public CreateShareParams setMaxDownloadTimes(Integer times){
        if(times == null || times==0){
            maxDownloadTimes = -1;
        }else
            maxDownloadTimes = times;
        return this;
    }

}
