package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.Group;
import com.hanhai.cloud.exception.UnauthorizedAccess;
import com.hanhai.cloud.exception.UpdateException;
import com.hanhai.cloud.params.AddGroupParams;
import com.hanhai.cloud.params.AddGroupParams;
import com.hanhai.cloud.params.UpdGroupParams;
import com.hanhai.cloud.service.GroupService;
import com.hanhai.cloud.vo.GroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.transform.Result;
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

    @PostMapping("/group/groupAdd")
    @ResponseBody
    public R addGroup(@RequestBody @Validated AddGroupParams groupParams) throws UpdateException {
        groupService.insertGroup(groupParams);
        return new R(ResultCode.SUCCESS).setMsg("添加成功");
    }

    @PostMapping("/group/groupUpd")
    @ResponseBody
    public R updGroup(@RequestBody @Validated UpdGroupParams groupParams) throws UpdateException {

        Group groupById = groupService.getGroupById(groupParams.getGroupId());
        if (groupById==null || !groupById.getUserId().equals(StpUtil.getLoginIdAsLong())){
            throw new UnauthorizedAccess();
        }

        groupService.updGroupName(groupParams);
        return new R(ResultCode.SUCCESS).setMsg("修改成功");
    }

//    @PostMapping("/group/groupDel") ResutFul
    @DeleteMapping("/group/{groupId}")
    @ResponseBody
    public R updGroup(@PathVariable(name = "groupId")
                      @Validated
                      @NotNull(message = "组id不能为空")
                      @Min(value = 1,message = "id非法")
                      Long groupId) throws  UpdateException {
        Group groupById = groupService.getGroupById(groupId);

        if (groupById==null || !groupById.getUserId().equals(StpUtil.getLoginIdAsLong())){
            throw new UnauthorizedAccess();
        }
        groupService.delGroup(groupId);
        return new R(ResultCode.SUCCESS).setMsg("删除成功");
    }
}
