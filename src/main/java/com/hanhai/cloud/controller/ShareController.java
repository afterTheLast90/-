package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.hanhai.cloud.base.PageResult;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.configuration.SystemInfo;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.UserShare;
import com.hanhai.cloud.exception.UnauthorizedAccess;
import com.hanhai.cloud.params.UserShareParams;
import com.hanhai.cloud.service.ShareService;
import com.hanhai.cloud.vo.UserShareVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

@Controller
public class ShareController {
    @Autowired
    private ShareService shareService;
    @Autowired
    private SystemInfo systemInfo;

    @GetMapping("/share")
    public String sharePage() {
        return "share";
    }

    @GetMapping("/share/shareGet")
    @ResponseBody
    public R<PageResult> getUserShare(UserShareParams userShareParams) {
        return new R(ResultCode.SUCCESS_NO_SHOW)
                    .setData(new PageResult(shareService.getUserShare(StpUtil.getLoginIdAsLong(), userShareParams)));
    }

    @PutMapping("/share/closeShare/{shareId}")
    @ResponseBody
    public R closeShare(@PathVariable(name = "shareId")
                            @NotNull(message = "id不能为空")
                            @Min(value = 1, message = "id非法") Long shareId) {
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
    public R<UserShareVO> getShareDetail(@RequestParam("shareId")Long shareId) {
        // 测试时删除
        systemInfo.setSiteUrl("http://localhost:8080");
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
        QrConfig config = new QrConfig(300,300);
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
}