package com.hanhai.cloud.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class GetShareVO{
    /** 用户id */
    private Long userId;
    /** 分享id */
    private String shareId;
    /** 文件名 */
    private String fileName;
    /** 文件类型 */
    private String fileType;
    /** 分享类型;
     0-公有分享
     1-企业内分享（登录后可以查看）
     2-私有分享（指定人查看）
     3-密码查看
     4-仅通过访问码查看 */
    private int shareType;
    /** 最大下载次数 */
    private Integer maxDownloadTimes;
    /** 最大转存次数 */
    private Integer maxFileDumpTimes;
    /** 下载次数 */
    private Integer downloadTimes;
    /** 转存次数 */
    private Integer fileDumpTime;
    /** 失效日期 */
    private LocalDateTime expireTime;
    /** 分享时间 */
    private LocalDateTime createdTime;
    /** 文件大小 */
    private Long fileSize;
    /** 是否可下载 */
    private Boolean haveDown;
    /** 是否可转存 */
    private Boolean haveDump;
    /** 是否有效 */
    private Boolean status;
    /** 当前路径 */
    private String currentPath;
    /** 用户文件id */
    private Long userFileId;


    // 可能status直接被映射为null，所以要提前为他设置值
    public GetShareVO setMaxDownloadTimes(Integer times) {
        this.maxDownloadTimes = times;
        setHaveDown();
        setStatus();
        return this;
    }

    public GetShareVO setMaxFileDumpTimes(Integer times) {
        this.maxFileDumpTimes = times;
        setHaveDump();
        setStatus();
        return this;
    }

    public GetShareVO setDownloadTimes(Integer times){
        this.downloadTimes = times;
        setHaveDown();
        setStatus();
        return this;
    }

    public GetShareVO setFileDumpTime(Integer times) {
        this.fileDumpTime = times;
        setHaveDump();
        setStatus();
        return this;
    }

    public GetShareVO setExpireTime(LocalDateTime localDateTime) {
        this.expireTime = localDateTime;
        setHaveDump();
        setHaveDown();
        setStatus();
        return this;
    }

    public GetShareVO setStatus() {
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


    public GetShareVO setHaveDown(){
        if(maxDownloadTimes!=null && downloadTimes!=null && expireTime!=null){
            this.haveDown = (maxDownloadTimes>downloadTimes || maxDownloadTimes==-1) &&
                            (expireTime.isAfter(LocalDateTime.now()) ||
                                    expireTime.isEqual(LocalDateTime.of(1970, 1, 1, 7, 59, 59))
                            );
        }
        return this;
    }

    public GetShareVO setHaveDump(){
        if(maxFileDumpTimes!=null && fileDumpTime!=null && expireTime!=null){
            this.haveDump = (maxFileDumpTimes>fileDumpTime || maxFileDumpTimes==-1) &&
                    (expireTime.isAfter(LocalDateTime.now()) ||
                            expireTime.isEqual(LocalDateTime.of(1970, 1, 1, 7, 59, 59))
                    );
        }
        return this;
    }
}
