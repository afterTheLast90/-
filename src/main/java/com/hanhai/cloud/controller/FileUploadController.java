package com.hanhai.cloud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FileUploadController {
    @GetMapping("/fileUpload")
    public String fileUpload(){
        return "file_upload";
    }

}
