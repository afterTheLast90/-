package com.hanhai.cloud.vo;

import com.hanhai.cloud.configuration.SystemInfo;
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
    /** 文件父目录 */
    private String fileParentPath ;
    /** 文件类型 */
    private String fileType;
    /** 分享类型;
     0-公有分享
     1-企业内分享（登录后可以查看）
     2-私有分享（指定人查看）
     3-密码查看
     4-仅通过访问码查看 */
    private String shareType ;
    /** 分享人/群组(share_type=2) */
    private ShareMumbersVO shareMumbersVO;

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
    /** 密码(share=3) */
    private String sharePassword ;
    /** 分享链接 */
    private String shareUrl;

    /** 是否有效 */
    private Boolean status;

    public UserShareVO setShareUrl(String url) {
        this.shareUrl = url+"/s/"+shareId;
        return this;
    }

    public UserShareVO setShareType(String type) {
        if(type.equals("0")) {
            shareType = "公有分享";
        }
        else if(type.equals("1")){
            shareType = "内部分享";
        }
        else if(type.equals("2")) {
            shareType = "私有分享";
        }
        else if(type.equals("3")) {
            shareType = "密码分享";
        }
        else if(type.equals("4")) {
            shareType = "访问码分享";
        }

        return this;
    }

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
        }
        return this;
    }
}
