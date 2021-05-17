package com.hanhai.cloud.service;

import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.Tag;
import com.hanhai.cloud.utils.BeanUtils;
import com.hanhai.cloud.vo.TagVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService extends BaseService {
    public List<TagVO> getAllTag(String tagName) {
//        List<Tag> tags = tagMapper.getByUserIdAndTagName(StpUtil.getLoginIdAsLong(), tagName);
//        List<TagVO> tagVOList = new ArrayList<TagVO>(tags.size());
//        for(Tag tag : tags) {
//            tagVOList.add(BeanUtils.convertTo(tag, TagVO.class));
//        }
//        return tagVOList;

        return tagMapper.getByUserIdAndTagName(StpUtil.getLoginIdAsLong(), tagName)
                .stream()
                .map(i -> BeanUtils.convertTo(i, TagVO.class))
                .collect(Collectors.toList());
    }

    public void insertTag(Tag tag) {
        tagMapper.insert(tag);
    }

    public void delTag(Long tagId) {
        tagMapper.deleteById(tagId);
    }

    public Tag getTagByName(Long tagOwner, String tagName) {
        return tagMapper.getTagByName(tagOwner, tagName);
    }

    public Tag getTagById(Long tagId) {
        return tagMapper.selectById(tagId);
    }
}
