package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.User;
import com.hanhai.cloud.params.LoginParams;
import com.hanhai.cloud.service.UserService;
import com.hanhai.cloud.utils.PasswordEncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wmgx
 * @create 2021-04-29-22:44
 **/
@Controller
@Slf4j
public class LoginController {
    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {

        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 40,4,4);
        request.getSession().setAttribute("captcha",lineCaptcha.getCode());
        response.setContentType("image/png");
        lineCaptcha.write(response.getOutputStream());
        response.getOutputStream().close();
    }

    @PostMapping("/login")
    @ResponseBody
    public R loginSystem(HttpServletRequest request,
                         @RequestBody  @Validated  LoginParams loginParams){
        log.info("请求登录"+loginParams.toString());
        if (!loginParams.getCaptcha().equals(request.getSession().getAttribute("captcha")))
            return new R(ResultCode.CAPTCHA_ERROR);
        request.getSession().setAttribute("captcha",null);
        User user = userService.getUserByEmailAndPhone(loginParams.getUsername());
        if (user==null)
            return new R(ResultCode.LOGIN_ERROR);

        if (PasswordEncryptionUtils.checkPassword(loginParams.getPassword(),user.getUserPassword())) {
            StpUtil.setLoginId(user.getUserId());
            return new R(ResultCode.SUCCESS).setMsg("登录成功");
        }

        return new R(ResultCode.LOGIN_ERROR);


    }
}
