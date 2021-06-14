package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.entity.FileInbox;
import com.hanhai.cloud.params.ReceivingRecordCommitParam;
import com.hanhai.cloud.params.UserFileParam;
import com.hanhai.cloud.service.InboxCommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class InboxCommitController {
    @Autowired
    private InboxCommitService inboxCommitService;

    @GetMapping("/inboxError")
    public String inbox_commit_error(){
        return "inbox_commit_error";
    }

    @GetMapping("/inboxSuccess")
    public String inbox_commit_success(){
        return "inbox_commit_success";
    }

    @GetMapping("/inboxCommit/{inboxId}")
    public String inbox_commit(@PathVariable("inboxId") String path, Model model){
        List<FileInbox> fileInboxList=new ArrayList<>();
        fileInboxList=inboxCommitService.getInboxList(Long.parseLong(path));
        if(fileInboxList.size()==0){
            System.out.println("查询无效");
            model.addAttribute("errmsg","哎哟，链接不存在或已经失效！");
            return "inbox_commit_error";
        }
        if(LocalDateTime.now().isAfter(fileInboxList.get(0).getEndTime())){
            System.out.println("链接已经失效！");
            model.addAttribute("errmsg","哎哟，链接不存在或已经失效！");
            return "inbox_commit_error";
        }
        model.addAttribute("fileInbox",fileInboxList.get(0));
        String publisherName=inboxCommitService.getPublisherName(fileInboxList.get(0).getPublisher()); //通过发布者ID获取发布者姓名
        model.addAttribute("publisherName",publisherName);
        int commitType=fileInboxList.get(0).getCommitType(); //提交权限 0：全部用户；1：登陆用户.
        boolean isLogin=StpUtil.isLogin(); //获取当前用户是否登陆
        model.addAttribute("isLogin",isLogin);
        if(commitType==1 && !isLogin){  //只有权限为1 并且 当前用户登陆时 才能够提交
            model.addAttribute("isCommit",false);
            System.out.println("权限不够，需要登陆！");
            model.addAttribute("errmsg","权限不够，请登陆后再提交！");
            return "inbox_commit_error";
        } else{
            model.addAttribute("isCommit",true);
        }
        return "inbox_commit";
    }

    @PostMapping("/receiveRecordCommit")
    @ResponseBody
    public R inbox_commit(@RequestBody @Validated ReceivingRecordCommitParam params){
        inboxCommitService.receivingRecordCommit(params);
        return R.getSuccess().setMsg("上传成功！");
    }

    @PostMapping("/inboxCommitIsCover")
    @ResponseBody
    public Long noCover(UserFileParam param){
        return inboxCommitService.findByName(param.getPath(),param.getName(),param.getInputName(),param.getUserId());
    }
}
