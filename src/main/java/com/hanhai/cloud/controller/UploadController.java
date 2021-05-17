package com.hanhai.cloud.controller;

import com.hanhai.cloud.params.MultipartFileParam;
import com.hanhai.cloud.service.UploadFileService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@Controller("/UploadController")
public class UploadController {
    @Resource
    private UploadFileService uploadFileService;

    //上传文件
    @PostMapping("/upload")
    @ResponseBody
    public Map<String,Object> upload(MultipartFileParam param,
                                     @RequestParam(value="data",required=false)MultipartFile multipartFile)
                                    throws IOException{
        Map<String,Object> map=null;
        try{
            map=uploadFileService.realUpload(param,multipartFile);
        }catch(Exception e){
            e.printStackTrace();
        }
        return map;
    }

    //通过查找文件的md5值来判断是否上传
    @PostMapping("/isUpload")
    @ResponseBody
    public Map<String,Object> isUpload(MultipartFileParam param){
        return uploadFileService.findByFileMd5(param.getMd5());
    }

}
