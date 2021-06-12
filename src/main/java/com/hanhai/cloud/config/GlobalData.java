package com.hanhai.cloud.config;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.configuration.SystemInfo;
import com.hanhai.cloud.service.UserService;
import com.hanhai.cloud.utils.BeanUtils;
import com.hanhai.cloud.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
/**
 * @author wmgx
 * @create 2021-05-15-21:14
 **/
@ControllerAdvice
public class GlobalData {
    @Autowired
    private UserService userService;

    @Autowired
    private SystemInfo systemInfo;

    @ModelAttribute
    public void addUserInfoVO(Model model) {
        model.addAttribute("isLogin",StpUtil.isLogin());

        model.addAttribute("emailEnabled",systemInfo.getEmailEnabled());
        model.addAttribute("alySmsEnabled",systemInfo.getAlySmsEnabled());
        if (StpUtil.isLogin())
            model.addAttribute("userInfo",
                    BeanUtils.convertTo(userService.getUserById(StpUtil.getLoginIdAsLong()),
                            UserInfoVO.class));

    }
}
