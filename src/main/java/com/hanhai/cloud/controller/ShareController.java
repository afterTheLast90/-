package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.PageResult;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.UserShare;
import com.hanhai.cloud.exception.UnauthorizedAccess;
import com.hanhai.cloud.params.UserShareParams;
import com.hanhai.cloud.service.ShareService;
import com.hanhai.cloud.vo.UserShareVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
public class ShareController {
    @Autowired
    ShareService shareService;

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

    @PutMapping("share/closeShare/{shareId}")
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
}