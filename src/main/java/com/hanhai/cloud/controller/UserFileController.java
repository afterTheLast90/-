package com.hanhai.cloud.controller;

import com.hanhai.cloud.base.R;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.service.UserFileService;
import com.hanhai.cloud.utils.BeanUtils;
import com.hanhai.cloud.vo.UserDirListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wmgx
 * @create 2021-05-20-23:52
 **/
@Controller
public class UserFileController {
    @Autowired
    UserFileService userFileService;
    @GetMapping("/getUserDir")
    @ResponseBody
    public R<List<UserDirListVO>> getUserDirs(@RequestParam("path")String path){

        return new R<List<UserDirListVO>>(ResultCode.SUCCESS_NO_SHOW).setData(userFileService.getDir(path).stream().map(i-> BeanUtils.convertTo(i,
                UserDirListVO.class)).collect(Collectors.toList())).setMsg("获取信息成功");
    }
}
