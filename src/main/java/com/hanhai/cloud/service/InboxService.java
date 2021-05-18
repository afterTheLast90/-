package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageHelper;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.base.PageParam;
import com.hanhai.cloud.entity.FileInbox;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InboxService extends BaseService {

    public List<FileInbox> getInboxListForCurrentUser(PageParam param){
        PageHelper.startPage(param.getPageNum(),param.getPageSize());
        return fileInboxMapper.getByUserId(StpUtil.getLoginIdAsLong());
    }
}
