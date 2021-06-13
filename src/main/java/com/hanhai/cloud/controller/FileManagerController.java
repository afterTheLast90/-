package com.hanhai.cloud.controller;

import com.hanhai.cloud.base.PageParam;
import com.hanhai.cloud.base.PageResult;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.configuration.SystemInfo;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.Files;
import com.hanhai.cloud.service.FileService;
import com.hanhai.cloud.service.RecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

/**
 * @author wmgx
 * @create 2021-06-12-1:00
 **/
@Controller
public class FileManagerController {
    @Autowired
    private FileService fileService;
    @Autowired
    private SystemInfo systemInfo;
    @Autowired
    private RecycleService recycleService;

    @GetMapping("/fileManager")
    public String fileManagerPage(){
        return "fileManager";
    }

    @GetMapping("/admin/getAllFiles")
    @ResponseBody
    public R getAllFiles(@Validated PageParam param){
         return R.getSuccess().setData(new PageResult(fileService.getAllFiles(param)));
    }
    @DeleteMapping("/admin/file/{fileId}")
    @ResponseBody
    public R deleteFile(@PathVariable("fileId") Long fileId){
        Files files = fileService.getById(fileId);
        File file = new File(systemInfo.getUpLoadPath() + "Uploads/" + files.getFilePath());
        if (!file.exists() || file.delete()){
            fileService.delete(fileId);
            return R.getSuccess().setMsg("删除成功");
        }
        return new R(ResultCode.UPDATE_ERROR).setMsg("删除失败");
    }

    @DeleteMapping("/admin/file/no")
    @ResponseBody
    public R deleteFile(){
        List<Files> allZerroCitationsCount = fileService.getAllZerroCitationsCount();
        for (Files files : allZerroCitationsCount) {
            File file = new File(systemInfo.getUpLoadPath() + "Uploads/" + files.getFilePath());
            if (!file.exists() || file.delete()){
                fileService.delete(files.getFileId());
            }
        }
        return R.getSuccess().setMsg("删除成功");

    }

    @GetMapping ("/admin/checkRecycle")
    @ResponseBody
    public R checkRecycle(){
        recycleService.update30DaysNotDelete();
        return R.getSuccess().setMsg("整理成功");
    }



}
