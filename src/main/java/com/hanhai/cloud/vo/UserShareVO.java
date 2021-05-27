package com.hanhai.cloud.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class UserShareVO {
    /** 分享id */
    private String shareId ;
    /** 文件名 */
    private String fileName ;

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
    /** 下载次数 */
    private Integer downloadTimes ;
    /** 转存次数 */
    private Integer fileDumpTime ;
    /** 过期时间;-1为永不过期即 1970-01-01T07:59:59 */
    private LocalDateTime expireTime ;
    /** 分享时间 */
    private LocalDateTime createdTime;
    /** 密码 */
    private String sharePassword ;

    /** 是否有效 */
    private Boolean status;

    // 可能status直接被映射为null，所以要提前为他设置值
    public UserShareVO setMaxDownloadTimes(Integer times) {
        this.maxDownloadTimes = times;
        setStatus();
        return this;
    }

    public UserShareVO setMaxFileDumpTimes(Integer times) {
        this.maxFileDumpTimes = times;
        setStatus();
        return this;
    }

    public UserShareVO setDownloadTimes(Integer times){
        this.downloadTimes = times;
        setStatus();
        return this;
    }

    public UserShareVO setFileDumpTime(Integer times) {
        this.fileDumpTime = times;
        setStatus();
        return this;
    }

    public UserShareVO setExpireTime(LocalDateTime localDateTime) {
        this.expireTime = localDateTime;
        setStatus();
        return this;
    }

    public UserShareVO setStatus() {
        if(maxDownloadTimes!=null && downloadTimes!=null && maxFileDumpTimes!=null && fileDumpTime!=null && expireTime!=null){
            this.status = (maxDownloadTimes>downloadTimes ||
                    maxFileDumpTimes>fileDumpTime ||
                    maxDownloadTimes==-1 ||
                    maxFileDumpTimes==-1) &&
                    (expireTime.isAfter(LocalDateTime.now()) ||
                            expireTime.isEqual(LocalDateTime.of(1970, 1, 1, 7, 59, 59))
                    );
            System.out.println(status);
        }
        return this;
    }
}
