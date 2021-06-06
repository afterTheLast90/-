package com.hanhai.cloud.service;

import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.entity.TagRelationship;
import com.hanhai.cloud.params.TagFilesParams;
import com.hanhai.cloud.vo.TagFilesVO;
import com.hanhai.cloud.vo.TagVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class TagRelationService extends BaseService {
    // 删除tag时，级联删除 标签关联的文件
    public void delByTagId(Long tagId){
        tagRelationshipMapper.delByTagId(tagId);
    }

    // 根据id(+ 文件名模糊搜索），得到文件信息
    public List<TagFilesVO> getFilesByTag(TagFilesParams tagFilesParams) {
        startPage(tagFilesParams);
        return tagRelationshipMapper.getFilesByTag(tagFilesParams);
    }

    // 删除文件上的标签
    public void delByRelationId(Long relationId) {
        tagRelationshipMapper.deleteById(relationId);
    }

    // 得到文件使用的所有标签
    public List<TagVO> getFileTags(Long userFileId){
        return tagRelationshipMapper.getFileTags(userFileId);
    }

    // 更新使用该标签的 文件数量
    public void updateFileCount(Long tagId){
        tagRelationshipMapper.updateFileCount(tagId);
    }

    // 查找文件 未使用的标签名和标签id
    public List<TagVO> getUnUseTags(Long tagOwner, Long userFileId,String tagName){
        return tagRelationshipMapper.getUnUseTags(tagOwner, userFileId, tagName);
    }

    // 文件添加标签
    public TagRelationship addFileTag(Long userFileId, Long tagId){
        TagRelationship tagRelationship = new TagRelationship().setTagId(tagId).setUserFileId(userFileId);
        tagRelationshipMapper.insert(tagRelationship);
        return tagRelationship;
    }

}
