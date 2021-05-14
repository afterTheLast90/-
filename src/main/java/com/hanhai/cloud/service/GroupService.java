package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageHelper;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.Group;
import com.hanhai.cloud.entity.GroupRelationship;
import com.hanhai.cloud.exception.UpdateException;
import com.hanhai.cloud.params.GroupParams;
import com.hanhai.cloud.utils.BeanUtils;
import com.hanhai.cloud.vo.GroupVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService extends BaseService {
    public List<GroupVO> getAllGroup(String groupName){
        List<Group> groups = groupMapper.getByUserIdAndGroupName(StpUtil.getLoginIdAsLong(), groupName);
        List<GroupVO> groupVOList = new ArrayList<>(groups.size());
        for (Group group : groups) {
            groupVOList.add(BeanUtils.convertTo(group,GroupVO.class));
        }
        return groupVOList;

//        return groupMapper.getByUserIdAndGroupName(StpUtil.getLoginIdAsLong(),groupName)
//                .stream()
//                .map(i-> BeanUtils.convertTo(i,GroupVO.class))
//                .collect(Collectors.toList());
    }

    @Transactional
    public void insertGroup(GroupParams  groupParams) throws UpdateException {
        Group group = new Group()
                .setGroupName(groupParams.getGroupName())
                .setNumberOfPersones(groupParams.getUserList().size())
                .setUserId(StpUtil.getLoginIdAsLong());
        System.out.println(group);
        groupMapper.insert(group);

        for (Long userId : groupParams.getUserList()) {
            groupRelationshipMapper.insert(new GroupRelationship().setGroupId(group.getGroupId()).setUserId(userId));
        }

    }
}
