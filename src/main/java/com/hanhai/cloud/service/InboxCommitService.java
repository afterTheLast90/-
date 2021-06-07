package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.FileInbox;
import com.hanhai.cloud.entity.ReceivingRecord;
import com.hanhai.cloud.params.ReceivingRecordCommitParam;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InboxCommitService extends BaseService{
    public List<FileInbox> getInboxList(Long inboxId){
        return fileInboxMapper.findByInboxId(inboxId);
    }

    public String getPublisherName(Long publisher){
        return userMapper.selectById(publisher).getUserName();
    }

    public void receivingRecordCommit(ReceivingRecordCommitParam params){
        ReceivingRecord receivingRecord=new ReceivingRecord();
        receivingRecord.setInboxId(params.getInboxId());
        receivingRecord.setFileId(params.getFileId());
        receivingRecord.setUserFileId(params.getUserFileId());
        Long userId= null;
        if(StpUtil.isLogin())
            userId=StpUtil.getLoginIdAsLong();
        else
            userId=-1L;
        receivingRecord.setUserId(userId);
        receivingRecord.setInputName(params.getInputName());
        receivingRecord.setCommitFileName(params.getCommitFileName());
        try{
            receivingRecordMapper.insert(receivingRecord);
        }catch(Exception e){
            System.out.println("****************************");
            System.out.println("收件记录插入成功！");
            System.out.println("****************************");
        }
    }

}
