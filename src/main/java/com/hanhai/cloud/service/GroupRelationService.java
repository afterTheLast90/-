package com.hanhai.cloud.service;

import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.Group;
import com.hanhai.cloud.exception.UpdateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupRelationService extends BaseService {

    @Transactional
    public void delByGroupId(Long groupId) throws UpdateException {
        groupRelationshipMapper.delByGroupId(groupId);
    }
}
