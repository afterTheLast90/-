package com.hanhai.cloud.service;

import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.UserShare;
import com.hanhai.cloud.params.UserShareParams;
import com.hanhai.cloud.vo.ShareMumbersVO;
import com.hanhai.cloud.vo.UserShareVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShareService extends BaseService {
    // 得到用户分享数据信息
    public List<UserShareVO> getUserShare(Long userId, UserShareParams userShareParams) {
        // 设置分页参数
        startPage(userShareParams);
        return userShareMapper.getUserShare(userId, userShareParams.getFileName());
    }

    // 关闭分享
    public void closeShare(Long shareId){
        userShareMapper.closeShare(LocalDateTime.now(), shareId);
    }

    // 根据分享id 得分享信息
    public UserShare getShareById(Long shareId) {
        return userShareMapper.selectById(shareId);
    }

    // 得到用户分享信息 详情
    public UserShareVO getShareDetail(Long shareId){
        UserShareVO shareDetail = userShareMapper.getShareDetail(shareId);
        ShareMumbersVO shareMumbersVO = new ShareMumbersVO()
                                            .setGroupName(innerShareMapper.getShardGroups(shareId))
                                            .setUserName(innerShareMapper.getShardUsers(shareId));
        shareDetail.setShareMumbersVO(shareMumbersVO);
        return shareDetail;
    }
}
