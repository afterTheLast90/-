package com.hanhai.cloud.service;

import com.hanhai.cloud.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class TagRelationService extends BaseService {
    public void delByTagId(Long tagId){
        tagRelationshipMapper.delByTagId(tagId);
    }
}
