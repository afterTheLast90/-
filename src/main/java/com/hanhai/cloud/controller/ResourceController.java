package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.PageResult;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.params.ResourceSearchParams;
import com.hanhai.cloud.service.ResourceService;
import com.hanhai.cloud.vo.ResourceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ResourceController {
    @Autowired
    ResourceService resourceService;

    @GetMapping("/resource")
    public String getResourcePage(Model model) {
        model.addAttribute("isLogin",StpUtil.isLogin());
        return "resourceSearch";
    }

    @GetMapping("/resource/resourceGet")
    @ResponseBody
    public R<PageResult> getResources(@Validated  ResourceSearchParams resourceSearchParams) {

        if(!StpUtil.isLogin()) {
            return new R<PageResult>(ResultCode.SUCCESS_NO_SHOW)
                                    .setData(new PageResult(resourceService.getPublicShare(resourceSearchParams)));
        }
        else
            return new R<PageResult>(ResultCode.SUCCESS_NO_SHOW)
                                .setData((new PageResult(resourceService.getUserShare(resourceSearchParams))));
    }
}
