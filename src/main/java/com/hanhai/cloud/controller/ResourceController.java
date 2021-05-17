package com.hanhai.cloud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ResourceController {
    @GetMapping("/resource")
    public String getResourcePage() {
        return "resourceSearch";
    }
}
