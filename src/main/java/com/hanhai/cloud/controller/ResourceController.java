package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.PageResult;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.User;
import com.hanhai.cloud.entity.UserFile;
import com.hanhai.cloud.entity.UserShare;
import com.hanhai.cloud.params.DumpResourceParams;
import com.hanhai.cloud.params.ResourceSearchParams;
import com.hanhai.cloud.service.ResourceService;
import com.hanhai.cloud.service.UserFileService;
import com.hanhai.cloud.vo.GetShareVO;
import com.hanhai.cloud.vo.ResourceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Controller
public class ResourceController {
    @Autowired
    ResourceService resourceService;
    @Autowired
    UserFileService userFileService;

    @GetMapping("/resource")
    public String getResourcePage(Model model) {
        model.addAttribute("isLogin", StpUtil.isLogin());

        return "resourceSearch";
    }

    // 得到公共/内部文件夹的 子目录资源文件
    @GetMapping("/resource/resourceGet")
    @ResponseBody
    public R<PageResult> getResources(@Validated ResourceSearchParams resourceSearchParams) {
        String currentPath = resourceSearchParams.getCurrentPath();
        if(currentPath.equals("null"))          // 初始化，第一次进入页面时
            currentPath = "";
        int layer = currentPath.split("/").length - 1;
        String shareId = resourceSearchParams.getShareId();
        // 进入子文件
        if(!resourceSearchParams.getGoUp()) {
            // 首次进入
            if(shareId.equals("")) {
                // 未登录时
                if(!StpUtil.isLogin()) {
                    return new R<PageResult>(ResultCode.SUCCESS_NO_SHOW)
                            .setData(new PageResult(resourceService.getPublicShare(resourceSearchParams)));
                }
                // 登录后
                return new R<PageResult>(ResultCode.SUCCESS_NO_SHOW)
                        .setData((new PageResult(resourceService.getUserPublicShare(resourceSearchParams))));
            }
            // 进入子目录
            String parentPath = resourceService.getParentPathByShareId(shareId);
            currentPath += resourceSearchParams.getUserFileId() + "/";
            System.out.println(parentPath + "----" + currentPath);
            resourceSearchParams.setCurrentPath(currentPath);
            List<ResourceVO> resourceVO = resourceService.getFileByPath(resourceSearchParams, parentPath);
            for (ResourceVO r : resourceVO) {           // 设置当前路径,子目录为相同的shareId
                r.setCurrentPath(resourceSearchParams.getCurrentPath());
                r.setShareId(shareId);
            }
            return new R<PageResult>(ResultCode.SUCCESS_NO_SHOW)
                    .setData(new PageResult(resourceVO));
        }
        // 返回上级目录
        // 当前根目录
        if(layer==0){
            if(!StpUtil.isLogin()) {
                return new R<PageResult>(ResultCode.SUCCESS_NO_SHOW)
                        .setData(new PageResult(resourceService.getPublicShare(resourceSearchParams)));
            }
            // 登录后
            return new R<PageResult>(ResultCode.SUCCESS_NO_SHOW)
                    .setData((new PageResult(resourceService.getUserPublicShare(resourceSearchParams))));
        }
        // 当前第N级目录(可转上级目录)
        String parentPath = resourceService.getParentPathByShareId(shareId);
        currentPath = currentPath.substring(0, currentPath.length()-1);
        currentPath = currentPath.substring(0, currentPath.lastIndexOf("/")+1);     // 得到上级目录
        resourceSearchParams.setCurrentPath(currentPath);
        List<ResourceVO> resourceVO = resourceService.getFileByPath(resourceSearchParams, parentPath);
        for (ResourceVO r : resourceVO) {
            r.setCurrentPath(currentPath);
            r.setShareId(shareId);
        }
        return new R<PageResult>(ResultCode.SUCCESS_NO_SHOW)
                .setData(new PageResult(resourceVO));
    }

    // 根据userFileId,判断当前用户是该文件的属主(true，则是属主)
    @GetMapping("/resource/checkOwner")
    @ResponseBody
    public R<Boolean> checkOwner(@RequestParam("userFileId")Long userFileId){
        UserFile userFile = userFileService.getFileById(userFileId);
        System.out.println(userFile.getUserId());
        System.out.println(StpUtil.getLoginIdAsLong());
        return new R(ResultCode.SUCCESS_NO_SHOW).setData(userFile.getUserId().equals(StpUtil.getLoginIdAsLong()));
    }

    // 访问文件分享页
    @GetMapping("/s/{shareId}")
    public String getShare(@PathVariable(name = "shareId") String shareId, Model model,
                           @RequestParam(required = false) String pwd) {
        // 得到用户文件分享详情
        GetShareVO getShareVO = resourceService.getShare(shareId);
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
        }
        model.addAttribute("fileType", fileType);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isLogin", isLogin);
        model.addAttribute("haveDown", getShareVO.getHaveDown());
        model.addAttribute("haveDump", getShareVO.getHaveDump());

        // 链接已失效
        if (!getShareVO.getStatus()) {
            errorInfo = "哎呀，你怎么才来啊，链接失效啦";
            model.addAttribute("errorInfo", errorInfo);
            return "getShareFail";
        }
        // 判断分享类型（0，3，4不需要登录，就可访问）
        if (shareType == 0) {
            // 未登录 且 链接无下载次数啦
            if(!isLogin && !getShareVO.getHaveDown()){
                errorInfo = "没有下载次数啦，登录后保存在自己网盘吧";
                model.addAttribute("errorInfo", errorInfo);
                return "getShareFail";
            }
            return "getShareFile";
        }
        if (shareType == 3) {
            // 未登录 且 链接无下载次数啦
            if(!isLogin && !getShareVO.getHaveDown()){
                errorInfo = "没有下载次数啦，登录后保存在自己网盘吧";
                model.addAttribute("errorInfo", errorInfo);
                return "getShareFail";
            }
            // 属主不用验证
            if (isLogin && isOwner) {
                return "getShareFile";
            }
            User user = resourceService.getUserByShareId(shareId);
            user.setUserName(user.getUserName().substring(0,1) + "**");
            model.addAttribute("user", user);
            // 第一次访问
            if (pwd == null) {
                model.addAttribute("pwdPass", false);        // 密码检测
                model.addAttribute("shareId", shareId);
                model.addAttribute("msg", "输入密码获取文件");
                return "getShareFile";
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
            return "getShareFile";

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
            return "getShareFile";
        }
        if (shareType == 2) {
            // 是否有访问权限
            Set<Long> userIds = resourceService.getAllUserId(shareId);
            // 有访问权限(或属主)
            if (userIds.contains(StpUtil.getLoginIdAsLong()) || isOwner) {
                return "getShareFile";
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

    // 得到分享页文件夹的 子目录文件
    @GetMapping("/s/shareInfo")
    @ResponseBody
    public R<List<GetShareVO>> getShareInfo(@Validated ResourceSearchParams searchParams,Model model){
        String currentPath = searchParams.getCurrentPath();
        String shareId = searchParams.getShareId();
        boolean isLogin = StpUtil.isLogin();
        boolean isOwner = false;
        int layer = currentPath.split("/").length - 1;      // 层数
        GetShareVO shareVO = resourceService.getShare(shareId);
        if (isLogin) {
            isOwner = shareVO.getUserId().equals(StpUtil.getLoginIdAsLong());
        }
        model.addAttribute("isOwner", isOwner);

        // 进入子文件
        if(searchParams.getGoUp()==null || !searchParams.getGoUp()) {
            // 首次进入
            if (currentPath.equals("init")) {
                // 得到用户文件分享详情
                return new R(ResultCode.SUCCESS_NO_SHOW).setData(Arrays.asList(shareVO));
            }
            // 进入子目录
            if(currentPath.equals("null"))          // 初始化，进入子目录
                currentPath = "";
            String parentPath = resourceService.getParentPathByShareId(shareId);
            currentPath += searchParams.getUserFileId() + "/";
            System.out.println(parentPath + "----" + currentPath);
            List<GetShareVO> shareVOS = resourceService.getShareByFolder(parentPath+currentPath, shareId);
            for(GetShareVO getShareVO : shareVOS){
                getShareVO.setCurrentPath(currentPath);
            }
            return new R(ResultCode.SUCCESS_NO_SHOW).setData(shareVOS);
        }
        // 返回上一级
        // 当前根目录
        if(layer==0){
            return new R(ResultCode.SUCCESS_NO_SHOW).setData(Arrays.asList(shareVO));
        }
        // 当前第N级目录(可转上级目录)
        String parentPath = resourceService.getParentPathByShareId(shareId);
        currentPath = currentPath.substring(0, currentPath.length()-1);
        currentPath = currentPath.substring(0, currentPath.lastIndexOf("/")+1);     // 得到上级目录
        List<GetShareVO> shareVOS = resourceService.getShareByFolder(parentPath+currentPath, shareId);
        for(GetShareVO getShareVO : shareVOS){
            getShareVO.setCurrentPath(currentPath);
        }
        return new R(ResultCode.SUCCESS_NO_SHOW).setData(shareVOS);
    }

    // 根据取件码，访问不同链接
    @GetMapping("/s/takeCode")
    public String takeCode(@RequestParam(name = "takeCode", required = false)
                              String takeCode, Model model){
        // 首次访问
        if(takeCode == null) {
            model.addAttribute("msg", "输入提取码获取文件");
            return "takeCode";
        }
        UserShare userShare = resourceService.getShareById(takeCode);
        // 没有对应信息
        if(userShare==null || userShare.getShareType()!=4){
            model.addAttribute("msg", "找不到对应的文件,请重新输入");
            return "takeCode";
        }
        // 得到对应文件
        model.addAttribute("shareId", takeCode);
        model.addAttribute("pwdPass", true);        // 不检测密码
        boolean isLogin = StpUtil.isLogin();
        boolean isOwner = false;
        if(isLogin)
            isOwner = userShare.getUserId().equals(StpUtil.getLoginIdAsLong());
        model.addAttribute("isLogin", isLogin);
        model.addAttribute("isOwner", isOwner);
        GetShareVO getShareVO = resourceService.getShare(takeCode);
        model.addAttribute("haveDown", getShareVO.getHaveDown());
        model.addAttribute("haveDump", getShareVO.getHaveDump());
        String fileType = getShareVO.getFileType().toLowerCase();
        model.addAttribute("fileType", fileType);
        // 取件码正确
        return "getShareFile";
    }


    // 根据文件类型，返回不同页面
//    private String getShareTypePath(String type) {
//        if (type.equals("dir")) {
//            return "getShareFolder";
//        } else
//            return "getShareFile";
//    }

    @PostMapping("/resource/dump")
    @ResponseBody
    public R resourceDump(@RequestBody DumpResourceParams resourceParams){
        resourceService.resourceDump(resourceParams.getUserFileIds(), resourceParams.getTargetPath(), resourceParams.getShareIds());
        return new R(ResultCode.SUCCESS).setData("保存成功");
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
}
