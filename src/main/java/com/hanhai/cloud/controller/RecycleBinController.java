package com.hanhai.cloud.controller;

import com.hanhai.cloud.entity.UserFile;
import com.hanhai.cloud.service.RecycleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RecycleBinController {
    @Resource
    RecycleService recycleService;


    @GetMapping("/RecycleBin")
    public String RecycleBinPage(Model model) {
        UserFile userFile= new UserFile();
        userFile.setFileId(123L);
        userFile.setFileName("大三").setUserFileId(123123L).setFileSize(1234L).setFileType("DIR").setCreatedTime(LocalDateTime.now()).setUpdatedTime(LocalDateTime.now());
        List<UserFile> userFileList = new ArrayList<>();
        for (int i = 0; i < 22; i++) {
            userFileList.add(userFile);
        }


    model.addAttribute("files",userFileList);
    return "RecycleBin";
    }
}