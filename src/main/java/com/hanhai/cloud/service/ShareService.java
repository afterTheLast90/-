package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.UserShare;
import com.hanhai.cloud.params.UserShareParams;
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
}
