package com.hanhai.cloud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InboxCommitController {
    @GetMapping("/inbox_commit")
    public String stu(){
        return "inbox_commit.html";
    }
}
