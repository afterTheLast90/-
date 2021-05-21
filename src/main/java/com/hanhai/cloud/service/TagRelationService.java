package com.hanhai.cloud.service;

import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.params.TagFilesParams;
import com.hanhai.cloud.vo.TagFilesVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

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

    public void delByRelationId(Long relationId) {
        tagRelationshipMapper.deleteById(relationId);
    }
}
