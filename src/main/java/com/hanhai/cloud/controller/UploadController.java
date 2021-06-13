package com.hanhai.cloud.controller;

import com.hanhai.cloud.entity.UserFile;
import com.hanhai.cloud.params.FastUploadParam;
import com.hanhai.cloud.params.MultipartFileParam;
import com.hanhai.cloud.params.UserFileParam;
import com.hanhai.cloud.service.UploadFileService;
import com.hanhai.cloud.vo.FileUploadVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Controller("/UploadController")
public class UploadController {
    @Resource
    private UploadFileService uploadFileService;

    //查找在虚拟目录下是否存在同名文件
    @PostMapping("/noCover")
    @ResponseBody
    public Long noCover(UserFileParam param){
        return uploadFileService.findByName(param.getPath(),param.getName(),param.getUserId());
    }

    //查找在虚拟目录下是否存在同名文件
    @PostMapping("/isCover")
    @ResponseBody
    public List<UserFile> isCover(UserFileParam param){
        return uploadFileService.findByNames(param.getPath(),param.getNames(),param.getUserId());
    }

    //通过查找文件的md5值来判断是否上传
    @PostMapping("/isUpload")
    @ResponseBody
    public FileUploadVO isUpload(MultipartFileParam param){
        return uploadFileService.findByFileMd5(param.getMd5());
    }

    //秒传逻辑，用户文件表插入记录或者修改记录
    @PostMapping("/fastUpload")
    @ResponseBody
    public Long fastUpload(FastUploadParam param){
        return uploadFileService.fastUpload(param);
    }

    //上传文件
    @PostMapping("/upload")
    @ResponseBody
    public FileUploadVO upload(MultipartFileParam param,
                                     @RequestParam(value="data",required=false)MultipartFile multipartFile)
                                    throws IOException{
        FileUploadVO fileUploadVO=null;
        try {
            fileUploadVO= uploadFileService.realUpload(param,multipartFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUploadVO;
    }

    //覆盖文件获取原文件大小
    @PostMapping("/getOriSize")
    @ResponseBody
    public Long getOriSize(FastUploadParam param){
        return uploadFileService.getOriSize(param);
    }

}
