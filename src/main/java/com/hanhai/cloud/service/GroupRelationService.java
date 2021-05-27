package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.Group;
import com.hanhai.cloud.entity.GroupRelationship;
import com.hanhai.cloud.exception.UpdateException;
import com.hanhai.cloud.params.GroupAddUserParam;
import com.hanhai.cloud.vo.GroupRelationVO;
import com.hanhai.cloud.vo.GroupUserSearchVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupRelationService extends BaseService {

    public void delByGroupId(Long groupId) throws UpdateException {
        groupRelationshipMapper.delByGroupId(groupId);
    }

    public List<GroupRelationVO> getGroupRelation(Long groupId){
        return groupRelationshipMapper.getGroupRelation(groupId);
    }

    public void delRelationById(Long groupRelationshipId) {
        groupRelationshipMapper.deleteById(groupRelationshipId);
    }

    public List<GroupUserSearchVO> searchUserByName(Long groupId, String userName){
        return groupRelationshipMapper.searchUserByName(groupId, userName, StpUtil.getLoginIdAsLong());
    }

    public List<GroupUserSearchVO> getUsersByName(String userName){
        return groupRelationshipMapper.getUsersByName(userName, StpUtil.getLoginIdAsLong());
    }

    @Transactional
    public void addRelation(GroupAddUserParam groupAddUserParam)throws UpdateException {
        for(Long userId : groupAddUserParam.getUserIds()){
            groupRelationshipMapper.insert(new GroupRelationship()
                                                .setGroupId(groupAddUserParam.getGroupId())
                                                .setUserId(userId));
        }
    }
}
