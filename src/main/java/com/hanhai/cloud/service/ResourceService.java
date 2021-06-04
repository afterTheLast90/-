package com.hanhai.cloud.service;

import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.params.ResourceSearchParams;
import com.hanhai.cloud.vo.GetShareVO;
import com.hanhai.cloud.vo.ResourceVO;
import com.hanhai.cloud.vo.ShareMumbersVO;
import com.hanhai.cloud.vo.UserShareVO;
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
}
