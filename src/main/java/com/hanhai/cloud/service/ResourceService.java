package com.hanhai.cloud.service;

import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.params.ResourceSearchParams;
import com.hanhai.cloud.vo.ResourceVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResourceService extends BaseService {
    public List<ResourceVO> getPublicShare(ResourceSearchParams resourceSearchParams) {
        startPage(resourceSearchParams);
        return userShareMapper.getPublicShare(LocalDateTime.now(), resourceSearchParams.getFileName());
    }

    public List<ResourceVO> getUserShare(ResourceSearchParams resourceSearchParams) {
        startPage(resourceSearchParams);
        return userShareMapper.getUserShare(LocalDateTime.now(), resourceSearchParams.getFileName());
    }
}
