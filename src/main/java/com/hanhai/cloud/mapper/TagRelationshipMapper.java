package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.TagRelationship;
import com.hanhai.cloud.params.TagFilesParams;
import com.hanhai.cloud.vo.TagFilesVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-27-16:25
 **/
public interface TagRelationshipMapper extends BaseMapper<TagRelationship> {
    @Update("update tag_relationship set deleted = true where tag_id = #{tagId}")
    public void delByTagId(@Param("tagId")Long tagId);

    // 得到 使用标签的文件信息
    @Select("select tag_relationship_id as tagRelationshipId, file_name, file_size, f.updated_time as updated_time " +
            "from user_files f, tag_relationship r, tags t " +
            "where f.user_file_id=r.user_file_id and r.tag_id=t.tag_id and " +
            "f.deleted=false and r.deleted=false and t.deleted=false and " +
            "r.tag_id=#{tagId} and file_name like concat('%',#{fileName},'%') ")
    public List<TagFilesVO> getFilesByTag(TagFilesParams tagFilesParams);
}
