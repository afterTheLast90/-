package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.entity.User;
import com.hanhai.cloud.params.UserInfoUpdateParams;
import com.hanhai.cloud.service.UserService;
import com.hanhai.cloud.utils.BeanUtils;
import com.hanhai.cloud.utils.utils.AvatarFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    AvatarFileUtils avatarFileUtils;
    @PutMapping("/userInfo/update")
    @ResponseBody
    public R updateUser(@RequestBody @Validated  UserInfoUpdateParams userInfoUpdateParams){
        System.out.println(userInfoUpdateParams);

        User userById = userService.getUserById(StpUtil.getLoginIdAsLong());

        BeanUtils.copyProperties(userInfoUpdateParams,userById);

        userService.updateById(userById);

        return R.getSuccess().setData(userById);
    }


    @Transactional
    @PostMapping("/userInfo/updateAvatar")
    @ResponseBody
    public R updateAvatar(MultipartFile file) throws IOException {
        String s = avatarFileUtils.saveFile(file);
        User userById = userService.getUserById(StpUtil.getLoginIdAsLong());
        userById.setUserAvatar(s);
        userService.updateById(userById);
        return R.getSuccess().setData(s);
    }


    @GetMapping("/avatar")
    public void getAvatar(@RequestParam(value = "avatar",required = true) String fileName, HttpServletResponse response) throws IOException {
//
//        InputStream file = avatarFileUtils.getFile(fileName);
//
//        response.getOutputStream().

        avatarFileUtils.getFileWrite(fileName,response);
    }
}
