package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.PageResult;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.params.ResourceSearchParams;
import com.hanhai.cloud.service.ResourceService;
import com.hanhai.cloud.service.ShareService;
import com.hanhai.cloud.vo.GetShareVO;
import com.hanhai.cloud.vo.ResourceVO;
import com.hanhai.cloud.vo.UserShareVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Controller
public class ResourceController {
    @Autowired
    ResourceService resourceService;

    @GetMapping("/resource")
    public String getResourcePage(Model model) {
        model.addAttribute("isLogin", StpUtil.isLogin());
        return "resourceSearch";
    }

    // 得到公共资源
    @GetMapping("/resource/resourceGet")
    @ResponseBody
    public R<PageResult> getResources(@Validated ResourceSearchParams resourceSearchParams) {
        // 未登录时
        if (!StpUtil.isLogin()) {
            return new R<PageResult>(ResultCode.SUCCESS_NO_SHOW)
                    .setData(new PageResult(resourceService.getPublicShare(resourceSearchParams)));
        }
        // 登录后
        else
            return new R<PageResult>(ResultCode.SUCCESS_NO_SHOW)
                    .setData((new PageResult(resourceService.getUserPublicShare(resourceSearchParams))));
    }

    @GetMapping("/s/{shareId}")
    public String getShare(@PathVariable(name = "shareId") String shareId, Model model,
                           @RequestParam(required = false) String pwd) {
        // 得到用户文件分享详情
        GetShareVO getShareVO = resourceService.getShare(shareId);
//        System.out.println(getShareVO.toString());
        String errorInfo = "";          // 错误信息
        model.addAttribute("pwdPass", true);        // 默认检测，(默认不检测，只在shareType=3时才设置)

        // 未得到有效分享文件信息（地址访问错误 || 文件被删除)
        if (getShareVO == null) {
            errorInfo = "哎哟，链接失效或链接地址错误了";
            model.addAttribute("errorInfo", errorInfo);
            return "getShareFail";
        }
        // 得到 有效分享文件信息
        // 根据 分享类型、文件类型、是否失效、  是否登录、是否文件属主、是否可下载、是否可上传 来进行不同访问与输出
        int shareType = getShareVO.getShareType();
        String fileType = getShareVO.getFileType().toLowerCase();
        boolean isLogin = StpUtil.isLogin();
        boolean isOwner = false;
        if (isLogin) {
            isOwner = getShareVO.getUserId().equals(StpUtil.getLoginIdAsLong());
            model.addAttribute("isOwner", isOwner);
        }
        model.addAttribute("isLogin", isLogin);

        // 链接已失效
        if (!getShareVO.getStatus()) {
            errorInfo = "哎呀，你怎么才来啊，链接失效啦";
            model.addAttribute("errorInfo", errorInfo);
            return "getShareFail";
        }
        // 判断分享类型（0，3，4不需要登录，就可访问）
        if (shareType == 0) {
            return getShareTypePath(fileType);
        }
        if (shareType == 3) {
            // 属主不用验证
            if (isLogin && isOwner) {
                return getShareTypePath(fileType);
            }
            // 第一次访问
            if (pwd == null) {
                model.addAttribute("pwdPass", false);        // 密码检测
                model.addAttribute("shareId", shareId);
                model.addAttribute("msg", "输入密码获取文件");
                return getShareTypePath(fileType);
            }
            // 检验密码
            String password = resourceService.getPwdByShareId(shareId);
            model.addAttribute("shareId", shareId);
            if (password.equals(pwd)) {
                model.addAttribute("pwdPass", true);        // 密码通过
            } else {
                model.addAttribute("pwdPass", false);        // 密码未通过
                model.addAttribute("msg", "密码输入错误");
            }
            return getShareTypePath(fileType);

        }
        if (shareType == 4) {
            errorInfo = "这是取件码分享，点界面右上角按钮来取件哦";
            model.addAttribute("errorInfo", errorInfo);
            return "getShareFail";
        }

        // 1 和2必须登录才能查看
        if (!isLogin) {
            errorInfo = "这个是内部文件分享啦，登录后才让你看";
            model.addAttribute("errorInfo", errorInfo);
            return "getShareFail";
        }
        if (shareType == 1) {
            return getShareTypePath(fileType);
        }
        if (shareType == 2) {
            // 是否有访问权限
            Set<Long> userIds = resourceService.getAllUserId(shareId);
            // 有访问权限
            if (userIds.contains(StpUtil.getLoginIdAsLong()) || isOwner) {
                return getShareTypePath(fileType);
            }
            // 无访问权限
            errorInfo = "人家没有分享给你看啦,死心吧";
            model.addAttribute("errorInfo", errorInfo);
            return "getShareFail";
        }
        errorInfo = "未知错误";
        model.addAttribute("errorInfo", errorInfo);
        return "getShareFail";
    }

//    // 链接失效界面测试
//    @GetMapping("/getShareFail")
//    public String getShareFail(){
//        return "getShareFail";
//    }
//    // 分享文件界面测试
//    @GetMapping("/getShareFile")
//    public String getShareFile() {
//        return "getShareFile";
//    }
//    // 分享文件夹界面测试
//    @GetMapping("/getShareFolder")
//    public String getShareFolder(){
//        return "getShareFolder";
//    }

    @GetMapping("s/shareInfo")
    @ResponseBody
    public R getShareInfo(@RequestParam("shareId")String shareId,Model model){
        // 得到用户文件分享详情
        GetShareVO getShareVO = resourceService.getShare(shareId);
        boolean isLogin = StpUtil.isLogin();
        boolean isOwner = false;
        if (isLogin) {
            isOwner = getShareVO.getUserId().equals(StpUtil.getLoginIdAsLong());
        }
        model.addAttribute(isOwner);

        return new R(ResultCode.SUCCESS_NO_SHOW).setData(Arrays.asList(getShareVO));
    }

    private String getShareTypePath(String type) {
        if (type.equals("dir")) {
            return "getShareFolder";
        } else
            return "getShareFile";
    }
}
