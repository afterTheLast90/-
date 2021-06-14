package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.FileInbox;
import com.hanhai.cloud.entity.ReceivingRecord;
import com.hanhai.cloud.entity.UserFile;
import com.hanhai.cloud.params.ReceivingRecordCommitParam;
import com.hanhai.cloud.utils.FileNameUtil;
import org.apache.xmlbeans.impl.common.NameUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InboxCommitService extends BaseService{

    /**
     *  通过名字查找虚拟目录是否存在同名文件
     */
    public Long findByName(String path, String name ,String inputName , String userId){
        inputName=inputName+"."+FileNameUtil.getExtensionName(name);
        System.out.println("===================================");
        System.out.println("path="+path);
        System.out.println("name="+name);
        System.out.println("inputName="+inputName);
        System.out.println("userId="+userId);
        System.out.println("===================================");
        List<UserFile> lists=new ArrayList<UserFile>();
        Long userFileId=null;
        lists=userFileMapper.getByName(path,inputName,Long.parseLong(userId));
        for(UserFile userFile:lists){
            userFileId=userFile.getUserFileId();
            if(userFileId!=null)
                break;
        }
        System.out.println("===================================");
        System.out.println("userFileId="+userFileId);
        System.out.println("===================================");
        return userFileId;
    }


    public List<FileInbox> getInboxList(Long inboxId){
        return fileInboxMapper.findByInboxId(inboxId);
    }

    public String getPublisherName(Long publisher){
        return userMapper.selectById(publisher).getUserName();
    }

    public void receivingRecordCommit(ReceivingRecordCommitParam params){
        if(params.getIsCover()==1){  //覆盖逻辑
            System.out.println("===================覆盖逻辑================");
            List<ReceivingRecord> lists=new ArrayList<ReceivingRecord>();
            lists=receivingRecordMapper.getByName(params.getInboxId(),params.getInputName()+"."+FileNameUtil.getExtensionName(params.getCommitFileName()));
            ReceivingRecord receivingRecord=lists.get(0);
            receivingRecord.setOver("1");
            receivingRecordMapper.updateById(receivingRecord);
        }else{  //新建逻辑
            System.out.println("===================新建逻辑================");
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
            receivingRecord.setInputName(params.getInputName()+"."+FileNameUtil.getExtensionName(params.getCommitFileName()));
            receivingRecord.setCommitFileName(params.getCommitFileName());
            try{
                receivingRecordMapper.insert(receivingRecord);
            }catch(Exception e){
                System.out.println("****************************");
                System.err.println("收件记录插入失败！");
                System.out.println("****************************");
            }
            //收件箱人数+1
            FileInbox fileInbox=new FileInbox();
            FileInbox fileInbox1=new FileInbox();
            fileInbox.setInboxId(params.getInboxId());
            fileInbox1=fileInboxMapper.selectById(fileInbox);
            fileInbox1.setCommitCount(fileInbox1.getCommitCount()+1);
            fileInboxMapper.updateById(fileInbox1);
        }
    }
}
