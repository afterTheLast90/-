package com.hanhai.cloud.controller;

import com.hanhai.cloud.base.R;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.Group;
import com.hanhai.cloud.exception.UpdateException;
import com.hanhai.cloud.params.GroupParams;
import com.hanhai.cloud.service.GroupService;
import com.hanhai.cloud.vo.GroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GroupController {
    @Autowired
    private  GroupService groupService;
    @GetMapping("/group")
    public String groupPage() {
        return "group";
    }

    @GetMapping("/group/groups")
    @ResponseBody
    public R<List<GroupVO>> getGroups(@RequestParam(value = "groupName",required = false,defaultValue = "") String groupName) {
        return new R<List<GroupVO>>(ResultCode.SUCCESS_NO_SHOW)
                .setData(groupService.getAllGroup(groupName));
    }

    @PostMapping("/group")
    @ResponseBody
    public R addGroup(@RequestBody @Validated GroupParams groupParams) throws UpdateException {
        groupService.insertGroup(groupParams);
        return new R(ResultCode.SUCCESS).setMsg("添加成功");
    }
}
