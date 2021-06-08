package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.User;
import com.hanhai.cloud.entity.UserFile;
import com.hanhai.cloud.entity.UserShare;
import com.hanhai.cloud.exception.UpdateException;
import com.hanhai.cloud.params.ResourceSearchParams;
import com.hanhai.cloud.vo.GetShareVO;
import com.hanhai.cloud.vo.ResourceVO;
import com.hanhai.cloud.vo.ShareMumbersVO;
import com.hanhai.cloud.vo.UserShareVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ResourceService extends BaseService {
    // 得到公共资源分享（未登录）
    public List<ResourceVO> getPublicShare(ResourceSearchParams resourceSearchParams) {
        startPage(resourceSearchParams);
        return userShareMapper.getPublicShare(LocalDateTime.now(), resourceSearchParams.getFileName());
    }

    // 得到 公共&内部 资源分享（登录后）
    public List<ResourceVO> getUserPublicShare(ResourceSearchParams resourceSearchParams) {
        startPage(resourceSearchParams);
        return userShareMapper.getUserPublicShare(LocalDateTime.now(), resourceSearchParams.getFileName());
    }

    // 获取分享文件
    public GetShareVO getShare(String shareId){
        GetShareVO getShareVO = userShareMapper.getShare(shareId);
        return getShareVO;
    }

    // 根据shareId，得到分享相关信息
    public UserShare getShareById(String shareId){
        return userShareMapper.selectById(shareId);
    }

    // 获取分享文件夹的内容
    public List<GetShareVO> getShareByFolder(String path, String shareId){
        List<GetShareVO> shareVOS = userShareMapper.getShareByFolder(path);
        UserShare userShare = userShareMapper.selectById(shareId);
        for(GetShareVO shareVO : shareVOS){
            shareVO.setExpireTime(userShare.getExpireTime())
                    .setCreatedTime(userShare.getCreatedTime());
        }
        return shareVOS;
    }

    // 得到私有分享的所有userId
    public Set<Long> getAllUserId(String shareId){
        List<Long> userIds = innerShareMapper.getAllUserIdByShareId(shareId);
        Set<Long> userIdsByGroup = innerShareMapper.getAllUserIdByGroup(shareId);
        for(Long userId : userIds){
            userIdsByGroup.add(userId);
        }
        for(Long s : userIdsByGroup){
            System.out.print(s);
        }
        return userIdsByGroup;
    }

    // 得到 分享密码
    public String getPwdByShareId(String shareId){
        return userShareMapper.getPwdByShareId(shareId);
    }

    // 根据shareId,得到父目录路径
    public String getParentPathByShareId(String shareId){
        return userShareMapper.getParentPathByShareId(shareId);
    }

    // 根据filePath,得到子目录文件
    public List<ResourceVO> getFileByPath(ResourceSearchParams searchParams, String parentPath){
        startPage(searchParams);
        return userShareMapper.getFileByPath(parentPath + searchParams.getCurrentPath(), searchParams.getFileName());
    }

    // 根据shareId,得到用户信息
    public User getUserByShareId(String shareId) {
        return userShareMapper.getUserByShareId(shareId);
    }

    // 根据shareId,得到对应的类型
    public String getFileTypeByShareId(String shareId){
        return userShareMapper.getFileTypeByShareId(shareId);
    }

    // 转存文件
    @Transactional
    public void resourceDump(Long[] userFileIds, String targetPath, String[] shareIds) throws UpdateException {
        List<UserFile> sourceFiles =  userShareMapper.getFileByUFIds(userFileIds);              // 需要转存的文件
        List<UserFile> targetFiles = userShareMapper.getFileByParentPathAndUser(StpUtil.getLoginIdAsLong(), targetPath);   // 目标目录下的所有文件
        // 检测文件重名
        Set<String> targetFileName = new HashSet<String>(targetFiles.size());
        for(UserFile userFile : targetFiles){
            targetFileName.add(userFile.getFileName() + " " + userFile.getFileType().toLowerCase());
        }
        // 保存各个文件
        for(UserFile sourceFile : sourceFiles){
            sourceFile.setCreatedTime(null);
            sourceFile.setUpdatedTime(null);
            sourceFile.setUserId(StpUtil.getLoginIdAsLong());
            sourceFile.setShareCount(0);
            // 文件夹，判断重名&遍历子目录
            if(("DIR").equals(sourceFile.getFileType())){
                Queue<String> sourcePathQ = new LinkedList<String>();
                Queue<String> targetPathQ = new LinkedList<String>();
                // 判断重名
                if(targetFileName.contains(sourceFile.getFileName()+ " " +sourceFile.getFileType().toLowerCase())){
                    int i = 1;
                    while(targetFileName.contains(sourceFile.getFileName() + "("+i+")" + " " +sourceFile.getFileType().toLowerCase())){
                        i++;
                    }
                    sourceFile.setFileName(sourceFile.getFileName() + "("+i+")");
                }
                // 保存该文件夹
                sourcePathQ.add(sourceFile.getFileParentPath() + sourceFile.getUserFileId() + "/");      // 子目录列表
                sourceFile.setUserFileId(null);
                sourceFile.setFileParentPath(targetPath);
                userFileMapper.insert(sourceFile);
                targetPathQ.add(targetPath + sourceFile.getUserFileId() + "/");                 // 目标 子目录列表
                // 遍历子目录
                while(!sourcePathQ.isEmpty()){
                    List<UserFile> sFiles = userShareMapper.getFileByParentPath(sourcePathQ.element());
                    if(sFiles.size()==0){           // 为空目录，则判断其他文件
                        sourcePathQ.poll();
                        targetPathQ.poll();
                        continue;
                    }
                    for(UserFile s : sFiles){       // 非空目录，则判断是否含有文件夹，有的话继续遍历，没有则直接保存
                        s.setFileParentPath(targetPathQ.element());
                        s.setUserId(StpUtil.getLoginIdAsLong());
                        s.setCreatedTime(null);
                        s.setUpdatedTime(null);
                        s.setShareCount(0);
                        if(("DIR").equals(s.getFileType())){
                            sourcePathQ.add(sourcePathQ.element() + s.getUserFileId() + "/");
                            s.setUserFileId(null);
                            userFileMapper.insert(s);
                            targetPathQ.add(targetPathQ.element() + s.getUserFileId() + "/");
                        }
                        else {
                            s.setUserFileId(null);
                            userFileMapper.insert(s);
                        }
                    }
                    sourcePathQ.poll();
                    targetPathQ.poll();
                }
            }
            // 文件，需要判断重名
            else {
                sourceFile.setUserFileId(null);
                sourceFile.setFileParentPath(targetPath);
                int i = 1;
                int dotIndex = sourceFile.getFileName().lastIndexOf(".");
                // 文件判断重名
                if(targetFileName.contains(sourceFile.getFileName() + " " + sourceFile.getFileType().toLowerCase())){
                    String fileName,fileType;
                    if(dotIndex == -1){
                        fileName = sourceFile.getFileName();
                        fileType = "";
                    }else{
                        fileName = sourceFile.getFileName().substring(0, dotIndex);
                        fileType = sourceFile.getFileName().substring(dotIndex);
                    }
                    while(targetFileName.contains(fileName +"("+i+")" + fileType + " " + sourceFile.getFileType().toLowerCase())){
                        i++;
                    }
                    sourceFile.setFileName(fileName +"("+i+")" + fileType);
                }
                userFileMapper.insert(sourceFile);
            }
        }
        // 更新分享文件的 转存次数
        for(String shareId : shareIds){
            userShareMapper.addDumpTime(shareId);
        }
    }
}
