package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.*;
import com.hanhai.cloud.params.QueryShareParams;
import com.hanhai.cloud.utils.BeanUtils;
import com.hanhai.cloud.utils.GenUtils;
import com.hanhai.cloud.vo.GroupUserSearchVO;
import com.hanhai.cloud.vo.ShareMumbersVO;
import com.hanhai.cloud.vo.UserShareVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShareService extends BaseService {
    // 得到用户分享数据信息
    public List<UserShareVO> getUserShare(Long userId, QueryShareParams userShareParams) {
        // 设置分页参数
        startPage(userShareParams);
        return userShareMapper.getUserShare(userId, userShareParams.getFileName());
    }

    // 关闭分享
    public void closeShare(String shareId){
        userShareMapper.closeShare(LocalDateTime.now(), shareId);
    }

    // 根据分享id 得分享信息
    public UserShare getShareById(String shareId) {
        return userShareMapper.selectById(shareId);
    }

    // 得到用户分享信息 详情
    public UserShareVO getShareDetail(String shareId){
        UserShareVO shareDetail = userShareMapper.getShareDetail(shareId);
        ShareMumbersVO shareMumbersVO = new ShareMumbersVO()
                                            .setGroupName(innerShareMapper.getShardGroups(shareId))
                                            .setUserName(innerShareMapper.getShardUsers(shareId));
        shareDetail.setShareMumbersVO(shareMumbersVO);
        return shareDetail;
    }

    // 新建分享信息
    public UserShare addShare(UserShare userShare) {
        userShareMapper.insert(userShare);
        return userShare;
    }

    // 根据user_file_id得用户文件信息
    public UserFile getUserFileById(Long userFildId){
        return userFileMapper.selectById(userFildId);
    }

    // 根据 email/phone/name模糊搜索用户，不包自身
    public List<GroupUserSearchVO> getUserByName(String name){
        List<GroupUserSearchVO> lists = new ArrayList<GroupUserSearchVO>();
        for (User user : userMapper.getUserByName(name, StpUtil.getLoginIdAsLong())) {
            GroupUserSearchVO userSearchVO = BeanUtils.convertTo(user, GroupUserSearchVO.class);
            lists.add(userSearchVO);
        }
        return lists;
    }

    // 添加私有分享
    public UserShare addInnerShare(UserShare userShare, Long[] groupIds, Long[] userIds){
        // 插入分享记录
        userShare.setShareId(GenUtils.generateShortUuid().substring(0, 6));
        userShareMapper.insert(userShare);
        // 插入分享的群组
        if(groupIds!=null && groupIds.length!=0) {
            for (Long groupId : groupIds) {
                innerShareMapper.insert(new InnerShare()
                        .setShareId(userShare.getShareId())
                        .setGroupId(groupId)
                        .setUserId((long) -1));
            }
        }
        // 插入分享的用户
        if(userIds!=null && userIds.length!=0) {
            for (Long userId : userIds) {
                innerShareMapper.insert(new InnerShare()
                        .setShareId(userShare.getShareId())
                        .setUserId(userId)
                        .setGroupId((long) -1));
            }
        }
        // 回显数据
        return userShare;
    }

    // 检查 是否有下载次数
    public Boolean checkDownload(String shareId){
        UserShare userShare = userShareMapper.selectById(shareId);
        LocalDateTime expireTime = userShare.getExpireTime();
        System.out.println("%^&*("+userShare.toString());
        if((userShare.getMaxDownloadTimes()==-1 || userShare.getMaxDownloadTimes()>userShare.getDownloadTimes()) &&
                (expireTime.isEqual(LocalDateTime.of(1970, 1, 1, 7, 59, 59)) || expireTime.isAfter(LocalDateTime.now()))){
            return true;
        }
        return false;
    }

    // 检查 是否有转存次数
    public Boolean checkDump(String shareId){
        UserShare userShare = userShareMapper.selectById(shareId);
        LocalDateTime expireTime = userShare.getExpireTime();
        if((userShare.getMaxFileDumpTimes()==-1 || userShare.getMaxFileDumpTimes()>userShare.getFileDumpTime()) &&
                expireTime.isEqual(LocalDateTime.of(1970, 1, 1, 7, 59, 59)) || expireTime.isAfter(LocalDateTime.now())){
            return true;
        }
        return false;
    }
}
