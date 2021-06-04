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
import com.hanhai.cloud.params.GroupAddUserParam;
import com.hanhai.cloud.params.UpdGroupParams;
import com.hanhai.cloud.service.GroupRelationService;
import com.hanhai.cloud.service.GroupService;
import com.hanhai.cloud.vo.GroupRelationVO;
import com.hanhai.cloud.vo.GroupUserSearchVO;
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
import java.util.Map;

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

    // 得到组名列表
    @GetMapping("/group/groups")
    @ResponseBody
    public R<List<GroupVO>> getGroups(@RequestParam(value = "groupName",required = false,defaultValue = "") String groupName) {
        return new R<List<GroupVO>>(ResultCode.SUCCESS_NO_SHOW)
                .setData(groupService.getAllGroup(groupName));
    }

    // 添加群组（同时添加用户列表）
    @PostMapping("/group/groupAdd")
    @ResponseBody
    public R addGroup(@RequestBody @Validated AddGroupParams groupParams) throws UpdateException {
        Group group = groupService.getGroupByName(groupParams.getGroupName(), StpUtil.getLoginIdAsLong());
        // 查重
        if(group != null) {
            throw new UpdateException().setMsg("群组名重复");
        }
        // 插入新群组（并设置与用户关系）
        groupService.insertGroup(groupParams);

        return new R(ResultCode.SUCCESS).setMsg("添加成功");
    }

    // 修改群组
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

    // 删除群组（及其关联的联系）
    @Transactional
    @DeleteMapping("/group/delGroup/{groupId}")
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

    // 得到群组用户列表
    @GetMapping("/group/relationGet")
    @ResponseBody
    public R<List<GroupRelationVO>> getGroupRelation(@Min(value = 1, message = "id非法") Long groupId){
            Group groupById = groupService.getGroupById(groupId);
            if (groupById == null || !groupById.getUserId().equals(StpUtil.getLoginIdAsLong())) {
                throw new UnauthorizedAccess();
            }
            // 得到群组用户信息
            return new R<List<GroupRelationVO>>(ResultCode.SUCCESS_NO_SHOW)
                    .setData(groupRelationService.getGroupRelation(groupId));
    }

    // 群组删除用户
    @DeleteMapping("/delGroup/userDel")
    @ResponseBody
    @Transactional
    public R delRelationById(@RequestParam(value = "relationId")Long relationId,
                             @RequestParam(value = "groupId")Long groupId) throws UpdateException{
        // 删除关系纪录
        groupRelationService.delRelationById(relationId);
        // 组人数-1
        groupService.delPersonNum(groupId);

        return new R(ResultCode.SUCCESS).setMsg("删除成功");
    }

    // 群组添加新用户时，检索用户
    @GetMapping("/group/userSearch")
    @ResponseBody
    public R<List<GroupUserSearchVO>> searchUserByName(Long groupId, String userName){
        return new R<List<GroupUserSearchVO>>(ResultCode.SUCCESS_NO_SHOW)
                                .setData(groupRelationService.searchUserByName(groupId, userName));
    }

    // 添加新群组时，搜索用户
    @GetMapping("/group/addGroupUserSearch")
    @ResponseBody
    public R<List<GroupUserSearchVO>> getUsersByName(String userName) {
        return new R<List<GroupUserSearchVO>>(ResultCode.SUCCESS_NO_SHOW)
                                        .setData(groupRelationService.getUsersByName(userName));
    }

    // 添加用户，并更新数量
    @PostMapping("group/relationAdd")
    @ResponseBody
    public R addRelation(@RequestBody @Validated GroupAddUserParam groupAddUserParam) {

        // 插入关联数据
        groupRelationService.addRelation(groupAddUserParam);

        // 更新组关联 数目
        groupService.addPersonNum(groupAddUserParam);

        return new R(ResultCode.SUCCESS).setMsg("插入成功");
    }
}
