package com.hanhai.cloud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FileUploadController {
    @GetMapping("/stu")
    public String stu(){
        return "file_upload";
    }

}
