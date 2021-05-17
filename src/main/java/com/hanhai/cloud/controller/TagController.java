package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.Tag;
import com.hanhai.cloud.exception.UnauthorizedAccess;
import com.hanhai.cloud.exception.UpdateException;
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

    @GetMapping("/tag/getTags")
    @ResponseBody
    public R<List<TagVO>> getTags(@RequestParam(value = "tagName", required = false, defaultValue = "")String tagName){
        return new R<List<TagVO>>(ResultCode.SUCCESS_NO_SHOW)
                .setData(tagService.getAllTag(tagName));
    }

    @PostMapping("/tag/tagAdd")
    @ResponseBody
    public R<Long> addTag(@RequestParam("tagName")  @NotNull(message = "标签名不能为空")
                        @NotBlank(message = "标签名不能为空") String tagName) throws UpdateException {
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

    @Transactional
    @DeleteMapping("/tag/{tagId}")
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
}

