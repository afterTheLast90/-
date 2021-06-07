package com.hanhai.cloud.service;

import com.github.pagehelper.PageHelper;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.base.PageParam;
import com.hanhai.cloud.entity.ReceivingRecord;
import com.hanhai.cloud.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceivingRecordService extends BaseService {
    public List<ReceivingRecord> findByInboxId(PageParam param,Long recordId){
        PageHelper.startPage(param.getPageNum(),param.getPageSize());
        return receivingRecordMapper.getByUserId(recordId);
    }

    public String getUserName(Long userId){
        User user = userMapper.selectById(userId);
        if (user==null)
            return "已注销用户";
        return user.getUserName();
    }
}
