package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.hanhai.cloud.base.PageResult;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.configuration.SystemInfo;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.UserFile;
import com.hanhai.cloud.entity.UserShare;
import com.hanhai.cloud.exception.UnauthorizedAccess;
import com.hanhai.cloud.params.AddInnerShare;
import com.hanhai.cloud.params.CreateShareParams;
import com.hanhai.cloud.params.QueryShareParams;
import com.hanhai.cloud.service.GroupService;
import com.hanhai.cloud.service.ShareService;
import com.hanhai.cloud.utils.BeanUtils;
import com.hanhai.cloud.utils.GenUtils;
import com.hanhai.cloud.vo.CreateShareVO;
import com.hanhai.cloud.vo.GroupUserSearchVO;
import com.hanhai.cloud.vo.GroupVO;
import com.hanhai.cloud.vo.UserShareVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Controller
public class ShareController {
    @Autowired
    private ShareService shareService;
    @Autowired
    private SystemInfo systemInfo;
    @Autowired
    private GroupService groupService;

    @GetMapping("/share")
    public String sharePage() {
        return "share";
    }

    // 得到用户分享信息
    @GetMapping("/share/shareGet")
    @ResponseBody
    public R<PageResult> getUserShare(QueryShareParams userShareParams) {
        return new R(ResultCode.SUCCESS_NO_SHOW)
                    .setData(new PageResult(shareService.getUserShare(StpUtil.getLoginIdAsLong(), userShareParams)));
    }

    // 使用分享失效
    @PutMapping("/share/closeShare/{shareId}")
    @ResponseBody
    public R closeShare(@PathVariable(name = "shareId")
                            @NotNull(message = "id不能为空")
                            @Min(value = 1, message = "id非法") String shareId) {
        // 判断非法操作
        UserShare userShare = shareService.getShareById(shareId);
        if(userShare==null || !userShare.getUserId().equals(StpUtil.getLoginIdAsLong())){
            throw new UnauthorizedAccess();
        }

        // 设置值
        shareService.closeShare(shareId);
        return new R(ResultCode.SUCCESS).setMsg("操作成功");
    }

    // 得到分享 详情细节
    @GetMapping("/share/shareDetail")
    @ResponseBody
    public R<UserShareVO> getShareDetail(@RequestParam("shareId")String shareId) {
        // 测试时删除
        systemInfo.setSiteUrl("http://localhost:8080/");
        return new R(ResultCode.SUCCESS_NO_SHOW)
                    .setData(shareService.getShareDetail(shareId).setShareUrl(systemInfo.getSiteUrl()));
    }

    // 生成二维码
    @GetMapping("/share/QRCode")
    public void getQRCode(@RequestParam("url") String url,
                        @RequestParam(value = "width", required = false, defaultValue = "300") Integer width,
                        @RequestParam(value = "height", required = false, defaultValue = "300") Integer height,
                            HttpServletResponse resp) throws IOException {
        // 设置 二维码
        QrConfig config = new QrConfig(width, height);
//        ClassPathResource resource = new ClassPathResource("/static/img/logo.png");
//        config.setImg(resource.getFile());                      // 设置中心图标
        config.setMargin(3);
        byte[] qrCode = QrCodeUtil.generatePng(url, config);    // 生成图片字节流

        resp.setContentType("image/png");                       // 设置返回类型
        OutputStream os = resp.getOutputStream();               // 输出到前端
        os.write(qrCode);
        os.flush();                     // 将缓冲区数据 冲刷出来
        os.close();
    }

    // 新建分享信息
    @PostMapping("/share/addShare")
    @ResponseBody
    public R<CreateShareVO> addShare(@RequestBody @Validated CreateShareParams shareParams) {
        // 是否非法操作
        UserFile userFileById = shareService.getUserFileById(shareParams.getUserFileId());
        if(userFileById == null || !userFileById.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new UnauthorizedAccess();
        }
        // 设置shareId & userId & downloadTimes & fileDumpTime
        UserShare userShare = BeanUtils.convertTo(shareParams, UserShare.class);
        userShare.setShareId(GenUtils.generateShortUuid().substring(0,6))
                    .setUserId(StpUtil.getLoginIdAsLong())
                    .setDownloadTimes(0)
                    .setFileDumpTime(0);
        // 插入数据
        userShare = shareService.addShare(userShare);
        // 返回 回显信息
        CreateShareVO createShareVO = BeanUtils.convertTo(userShare, CreateShareVO.class);
        createShareVO.setShareUrl("http://localhost:8080/");
        return new R(ResultCode.SUCCESS_NO_SHOW).setData(createShareVO);
    }

    // 搜索群组信息
    @GetMapping("/share/queryGroup")
    @ResponseBody
    public R<List<GroupVO>> queryGroup(@RequestParam("groupName") String groupName) {
        if(groupName == "") {
            return new R(ResultCode.SUCCESS_NO_SHOW);
        }
        else
            return new R(ResultCode.SUCCESS_NO_SHOW).setData(groupService.getAllGroup(groupName));
    }

    // 根据 email/phone/name模糊搜索用户，不包自身
    @GetMapping("/share/queryUser")
    @ResponseBody
    public R<List<GroupUserSearchVO>> queryUser(@RequestParam("userName")String userName){
        if(userName == "") {
            return new R(ResultCode.SUCCESS_NO_SHOW);
        }else
            return new R(ResultCode.SUCCESS_NO_SHOW).setData(shareService.getUserByName(userName));
    }

    // 私有分享
    @PostMapping("/share/addInnerShare")
    @ResponseBody
    public R<CreateShareVO> addInnerShare(@RequestBody AddInnerShare innerShare){
        // 是否非法操作
        UserFile userFileById = shareService.getUserFileById(innerShare.getUserFileId());
        if(userFileById == null || !userFileById.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new UnauthorizedAccess();
        }
        // 设置shareId & userId & downloadTimes & fileDumpTime
        UserShare userShare = BeanUtils.convertTo(innerShare, UserShare.class);
        userShare.setShareId(GenUtils.generateShortUuid().substring(0,6))
                .setUserId(StpUtil.getLoginIdAsLong())
                .setDownloadTimes(0)
                .setFileDumpTime(0);
        // 回显数据
        userShare = shareService.addInnerShare(userShare, innerShare.getGroupIds(), innerShare.getUserIds());
        CreateShareVO shareVO = BeanUtils.convertTo(userShare, CreateShareVO.class);
        shareVO.setShareUrl("http://localhost:8080/");
        return new R(ResultCode.SUCCESS).setData(shareVO);
    }

    // 取件码分享
    @PostMapping("/share/codeShare")
    @ResponseBody
    public R<CreateShareVO> addCodeShare(@RequestBody CreateShareParams shareParams){
        // 是否非法操作
        UserFile userFileById = shareService.getUserFileById(shareParams.getUserFileId());
        if(userFileById == null || !userFileById.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new UnauthorizedAccess();
        }
        // 设置shareId & userId & downloadTimes & fileDumpTime
        UserShare userShare = BeanUtils.convertTo(shareParams, UserShare.class);
        userShare.setShareId(GenUtils.generateShortUuid().substring(0,6))
                .setUserId(StpUtil.getLoginIdAsLong())
                .setDownloadTimes(0)
                .setFileDumpTime(0);
        // 回显数据
        userShare = shareService.addShare(userShare);
        CreateShareVO createShareVO = BeanUtils.convertTo(userShare, CreateShareVO.class);
        return new R(ResultCode.SUCCESS_NO_SHOW).setData(createShareVO);
    }
}