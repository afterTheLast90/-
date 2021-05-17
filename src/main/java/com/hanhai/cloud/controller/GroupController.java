package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.Group;
import com.hanhai.cloud.entity.GroupRelationship;
import com.hanhai.cloud.exception.UnauthorizedAccess;
import com.hanhai.cloud.exception.UpdateException;
import com.hanhai.cloud.params.AddGroupParams;
import com.hanhai.cloud.params.AddGroupParams;
import com.hanhai.cloud.params.UpdGroupParams;
import com.hanhai.cloud.service.GroupRelationService;
import com.hanhai.cloud.service.GroupService;
import com.hanhai.cloud.vo.GroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.transform.Result;
import java.util.List;

@Controller
@Validated
public class GroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupRelationService groupRelationService;

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
    public R addGroup(@RequestBody AddGroupParams groupParams) throws UpdateException {
        Group group = groupService.getGroupByName(groupParams.getGroupName(), StpUtil.getLoginIdAsLong());
        if(group != null) {
            throw new UpdateException().setMsg("群组名重复");
        }
        groupService.insertGroup(groupParams);
        return new R(ResultCode.SUCCESS).setMsg("添加成功");
    }

    @PutMapping("/group/groupUpd")
    @ResponseBody
    public R updGroup(@RequestBody UpdGroupParams groupParams) throws UpdateException {
        Group group = groupService.getGroupById(groupParams.getGroupId());
        if (group==null || !group.getUserId().equals(StpUtil.getLoginIdAsLong())){
            throw new UnauthorizedAccess();
        }
        Group groupByName = groupService.getGroupByName(groupParams.getGroupName(), StpUtil.getLoginIdAsLong());
        if(groupByName != null) {
            throw new UpdateException().setMsg("群组名重复");
        }
        group.setGroupName(groupParams.getGroupName());
        groupService.updGroupName(group);
        return new R(ResultCode.SUCCESS).setMsg("修改成功");
    }

    @Transactional
    @DeleteMapping("/group/{groupId}")
    @ResponseBody
    public R delGroup(@PathVariable(name = "groupId")
                      @NotNull(message = "组id不能为空")
                      @Min(value = 1,message = "id非法")
                      Long groupId) throws  UpdateException {

        Group groupById = groupService.getGroupById(groupId);
        if (groupById==null || !groupById.getUserId().equals(StpUtil.getLoginIdAsLong())){
            throw new UnauthorizedAccess();
        }
        groupService.delGroup(groupId);
        // 删除群组 里的用户关系
        groupRelationService.delByGroupId(groupId);
        return new R(ResultCode.SUCCESS).setMsg("删除成功");
    }
}
