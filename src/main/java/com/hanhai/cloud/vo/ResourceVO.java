package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


@Data
@Accessors(chain = true)
public class ResourceVO {
    /** 分享id */
    private String shareId ;
    /** 文件名*/
    private String fileName;
    /** 分享人*/
    private String userName;
    /** 分享时间 */
    private LocalDateTime createdTime;
    /** 过期时间;-1为永不过期即 1970-01-01T07:59:59 */
    private LocalDateTime expireTime ;
    /** 最大下载次数 */
    private Integer maxDownloadTimes ;
    /** 最大转存次数 */
    private Integer maxFileDumpTimes ;
    /** 下载次数 */
    private Integer downloadTimes ;
    /** 转存次数 */
    private Integer fileDumpTime ;
    /** 是否可下载 */
    private Boolean downloadAllow;
    /** 是否可转存 */
    private Boolean fileDumpAllow;

    public ResourceVO setDownloadTimes(Integer times){
        this.downloadTimes = times;
        if(downloadTimes != null && maxDownloadTimes != null){
            this.downloadAllow=(maxDownloadTimes==-1 || downloadTimes<maxDownloadTimes);
        }
        return this;
    }
    public ResourceVO setMaxDownloadTimes(Integer times){
        this.maxDownloadTimes = times;
        if(maxDownloadTimes != null && downloadTimes != null){
            this.downloadAllow=(maxDownloadTimes==-1 || downloadTimes<maxDownloadTimes);
      }
        return this;
    }

    public ResourceVO setMaxFileDumpTimes(Integer times){
        this.maxFileDumpTimes = times;
        if(maxFileDumpTimes != null && fileDumpTime != null){
            this.fileDumpAllow = (maxFileDumpTimes==-1 || fileDumpTime<maxFileDumpTimes);
        }
        return this;
    }

    public ResourceVO setFileDumpTime(Integer times){
        this.fileDumpTime = times;
        if(fileDumpTime != null && maxFileDumpTimes!=null){
            this.fileDumpAllow = (maxFileDumpTimes==-1 || fileDumpTime<maxFileDumpTimes);
        }
        return this;
    }


}
