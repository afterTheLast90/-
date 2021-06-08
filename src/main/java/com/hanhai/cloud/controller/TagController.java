package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.PageResult;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.Tag;
import com.hanhai.cloud.entity.TagRelationship;
import com.hanhai.cloud.exception.UnauthorizedAccess;
import com.hanhai.cloud.exception.UpdateException;
import com.hanhai.cloud.params.AddTagRelationParams;
import com.hanhai.cloud.params.DelTagParams;
import com.hanhai.cloud.params.TagFilesParams;
import com.hanhai.cloud.params.UpdTagParams;
import com.hanhai.cloud.service.TagRelationService;
import com.hanhai.cloud.service.TagService;
import com.hanhai.cloud.vo.TagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
@Validated
public class TagController {
    @Autowired
    TagService tagService;
    @Autowired
    TagRelationService tagRelationService;

    @GetMapping("/tag")
    public String getTagPage() {
        return "tag";
    }

    // 显示所有标签信息
    @GetMapping("/tag/getTags")
    @ResponseBody
    public R<List<TagVO>> getTags(@RequestParam(value = "tagName", required = false, defaultValue = "")String tagName){
        return new R<List<TagVO>>(ResultCode.SUCCESS_NO_SHOW)
                .setData(tagService.getAllTag(tagName));
    }

    // 添加标签
    @PostMapping("/tag/tagAdd")
    @ResponseBody
    public R<Long> addTag(@RequestParam("tagName")
                            @NotNull(message = "标签名不能为空")
                            @NotBlank(message = "标签名不能为空")
                            String tagName) throws UpdateException {
        // 标签名查重
        Tag tagByName = tagService.getTagByName(StpUtil.getLoginIdAsLong(), tagName);
        System.out.println(tagByName != null);
        if (tagByName != null) {
            System.out.println(11111);
            throw new UpdateException().setMsg("标签名重复");
        }
        // 插入新标签
        Tag tag  = new Tag().setTagName(tagName)
                            .setTagOwner(StpUtil.getLoginIdAsLong());
        tagService.insertTag(tag);
        return new R<Long>(ResultCode.SUCCESS).setMsg("添加成功")
                                            .setData(tag.getTagId());
    }

    // 删除标签
    @Transactional
    @DeleteMapping("/tag/tagDel/{tagId}")
    @ResponseBody
    public R delTag(@PathVariable(name = "tagId")
                    @NotNull(message = "标签id不能为空")
                    @Min(value = 1, message = "id非法")
                    Long tagId) throws UpdateException {
        // 验证是否合法操作
        Tag tagById = tagService.getTagById(tagId);
        if(tagById == null || !tagById.getTagOwner().equals(StpUtil.getLoginIdAsLong())) {
            throw new UnauthorizedAccess();
        }

        // 删除tag
        tagService.delTag(tagId);

        // 删除tag与文件关联的关系
        tagRelationService.delByTagId(tagId);

        return new R(ResultCode.SUCCESS).setMsg("删除成功");
    }

    // 修改标签名
    @PutMapping("/tag/tagUpd")
    @ResponseBody
    public R updTag(@RequestBody UpdTagParams tagParams) throws UpdateException {
        // 是否非法操作
        Tag tagById = tagService.getTagById(tagParams.getTagId());
        if(tagById==null || !tagById.getTagOwner().equals(StpUtil.getLoginIdAsLong())){
            throw new UnauthorizedAccess();
        }
        // 是否重名
        tagById = tagService.getTagByName(StpUtil.getLoginIdAsLong(), tagParams.getTagName());
        if(tagById != null) {
            throw new UpdateException().setMsg("标签名重复");
        }
        // 修改tagName
        tagService.updTag(tagParams);

        return new R(ResultCode.SUCCESS).setMsg("修改成功");
    }

    // 得到标签对应的文件信息（+文件名模糊搜索）
    @GetMapping("/tag/tagFiles")
    @ResponseBody
    public R<PageResult> getTagFiles(TagFilesParams tagFilesParams) {
        // 当点击标签时
        if(tagFilesParams.getTagId() != null) {
            // 操作是否合法
            Tag tagById = tagService.getTagById(tagFilesParams.getTagId());
            if (tagById == null || !tagById.getTagOwner().equals(StpUtil.getLoginIdAsLong())) {
                throw new UnauthorizedAccess();
            }

            // 得到所有文件数据
            return new R<PageResult>(ResultCode.SUCCESS_NO_SHOW)
                    .setData(new PageResult(tagRelationService.getFilesByTag(tagFilesParams)));
        }
        // 未点击时
        else
            return new R<PageResult>(ResultCode.SUCCESS_NO_SHOW);
    }

    // 删除标签 与 单个文件的关联关系
    @DeleteMapping("/tag/relationDel")
    @ResponseBody
    public R delTagRelation(DelTagParams delTagParams){
        System.out.println(delTagParams.toString());
        tagRelationService.delByRelationId(delTagParams.getRelationId());
        tagRelationService.updateFileCount(delTagParams.getTagId());          // 更新文件使用该标签的数量
        return new R(ResultCode.SUCCESS).setMsg("删除成功");
    }

    // 删除标签 与 多个文件的关联关系
    @PutMapping("/tag/relationsDel")
    @Transactional
    @ResponseBody
    public R delTagRelations(@RequestBody DelTagParams delTagParams) throws UpdateException {
        System.out.println(delTagParams.toString());
        for(Long relationId : delTagParams.getTagRelationsId()) {
            tagRelationService.delByRelationId(relationId);
        }
        tagRelationService.updateFileCount(delTagParams.getTagId());          // 更新文件使用该标签的数量
        return new R(ResultCode.SUCCESS).setMsg("删除成功");
    }

    // 得到文件使用的所有标签
    @GetMapping("/tag/fileTagsGet")
    @ResponseBody
    public R<List<TagVO>> getFileTags(@RequestParam("userFileId") Long userFileId){
        return new R(ResultCode.SUCCESS_NO_SHOW).setData(tagRelationService.getFileTags(userFileId));
    }

    // 查找文件 未使用的标签名和标签id
    @GetMapping("/tag/getUnUseTags")
    @ResponseBody
    public R<List<TagVO>> getUnUseTags(@RequestParam("userFileId")Long userFileId, @RequestParam("tagName")String tagName) {
        return new R(ResultCode.SUCCESS_NO_SHOW).setData(tagRelationService.getUnUseTags(StpUtil.getLoginIdAsLong(), userFileId, tagName));
    }

    // 文件添加标签
    @PostMapping("tag/addFileTag")
    @ResponseBody
    public R<TagRelationship> addFileTag(@RequestBody AddTagRelationParams tagRelationParams){
        return new R(ResultCode.SUCCESS).setMsg("添加成功").setData(tagRelationService.addFileTag(tagRelationParams.getUserFileId(), tagRelationParams.getTagId()));
    }
}

