package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageHelper;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.base.PageParam;
import com.hanhai.cloud.entity.FileInbox;
import com.hanhai.cloud.params.FileInboxEndCommitParams;
import com.hanhai.cloud.params.FileInboxNewParams;
import com.hanhai.cloud.params.FileInboxUpdateInitParams;
import com.hanhai.cloud.params.FileInboxUpdateParams;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InboxService extends BaseService {

    public List<FileInbox> getInboxListForCurrentUser(PageParam param){
        PageHelper.startPage(param.getPageNum(),param.getPageSize());
        return fileInboxMapper.getByUserId(StpUtil.getLoginIdAsLong());
    }

    //新建收件任务，插入收件数据
    public Long newInboxTask(FileInboxNewParams params){
        FileInbox fileInbox=new FileInbox();
        fileInbox.setTitle(params.getTitle());
        fileInbox.setInputTips(params.getInputTips());
        fileInbox.setCommitType(params.getCommitType());
        fileInbox.setSavePathId(params.getSavePathId());
        fileInbox.setSavePath(params.getSavePath());
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        fileInbox.setEndTime(LocalDateTime.parse(params.getEndTime(),df));
        fileInbox.setContent(params.getContent());
        fileInbox.setPublisher(StpUtil.getLoginIdAsLong());
        fileInbox.setCommitCount(0);
        System.out.println(fileInbox);
        try{
            fileInboxMapper.insert(fileInbox);
        }catch (Exception e){
            System.out.println("****************************");
            System.out.println("插入出错了！");
            System.out.println("****************************");
            e.printStackTrace();
        }
        return fileInbox.getInboxId();
    }

    //截止提交
    public void endCommit(FileInboxEndCommitParams params){
        FileInbox fileInbox=new FileInbox();
        fileInbox.setInboxId(params.getInboxId());
        fileInbox.setEndTime(LocalDateTime.now());
        fileInboxMapper.updateById(fileInbox);
    }

    //修改收件箱任务
    public void updateInboxTask(FileInboxUpdateParams params){
        FileInbox fileInbox =new FileInbox();
        fileInbox.setInboxId(params.getInboxId());
        fileInbox.setTitle(params.getTitle());
        fileInbox.setInputTips(params.getInputTips());
        fileInbox.setCommitType(params.getCommitType());
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        fileInbox.setEndTime(LocalDateTime.parse(params.getEndTime(),df));
        fileInbox.setContent(params.getContent());
        try{
            fileInboxMapper.updateById(fileInbox);
        }catch (Exception e){
            System.out.println("****************************");
            System.out.println("更新出错了！");
            System.out.println("****************************");
            e.printStackTrace();
        }
    }

    //修改任务初始化,通过inboxId获取其他信息
    public List<FileInbox> updateInboxInit(FileInboxUpdateInitParams params){
        return fileInboxMapper.findByInboxId(params.getInboxId());
    }
}

