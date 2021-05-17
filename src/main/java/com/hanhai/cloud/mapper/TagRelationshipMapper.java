package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.TagRelationship;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author wmgx
 * @create 2021-04-27-16:25
 **/
public interface TagRelationshipMapper extends BaseMapper<TagRelationship> {
    @Update("update tag-relationship set deleted = true where tag_id = #{tagId}")
    public void delByTagId(@Param("tagId")Long tagId);
}
