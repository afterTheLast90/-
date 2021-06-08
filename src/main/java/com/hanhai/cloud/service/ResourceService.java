package com.hanhai.cloud.service;

import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.User;
import com.hanhai.cloud.entity.UserShare;
import com.hanhai.cloud.params.ResourceSearchParams;
import com.hanhai.cloud.vo.GetShareVO;
import com.hanhai.cloud.vo.ResourceVO;
import com.hanhai.cloud.vo.ShareMumbersVO;
import com.hanhai.cloud.vo.UserShareVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
}
