package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.TagRelationship;
import com.hanhai.cloud.params.TagFilesParams;
import com.hanhai.cloud.vo.TagFilesVO;
import com.hanhai.cloud.vo.TagVO;
import org.apache.ibatis.annotations.Insert;
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
    @Select("select f.user_file_id as user_file_id, tag_relationship_id as tagRelationshipId, file_name, file_type, file_size, f.updated_time as updated_time " +
            "from user_files f, tag_relationship r, tags t " +
            "where f.user_file_id=r.user_file_id and r.tag_id=t.tag_id and " +
            "f.deleted=false and r.deleted=false and t.deleted=false and " +
            "r.tag_id=#{tagId} and file_name like concat('%',#{fileName},'%') ")
    public List<TagFilesVO> getFilesByTag(TagFilesParams tagFilesParams);

    // 得到文件使用的所有标签
    @Select("select tag_relationship_id, tag_name, file_count\n" +
            "from tag_relationship r, tags t\n" +
            "where r.tag_id=t.tag_id and user_file_id=#{userFileId} and " +
            "r.deleted=false and t.deleted=false")
    public List<TagVO> getFileTags(@Param("userFileId")Long userFileId);

    // 更新使用该标签的 文件数量
    @Update("update tags set file_count=\n" +
            "(select count(*) from tag_relationship r\n" +
            "    where r.tag_id=#{tagId} and deleted=false)\n" +
            "where tag_id=#{tagId}")
    public void updateFileCount(@Param("tagId")Long tagId);

    // 查找文件 未使用的标签名和标签id
    @Select("select tag_id, tag_name from tags\n" +
            "    where tag_owner=#{tagOwner} and deleted=false\n" +
            "      and tag_id not in\n" +
            "            (select t.tag_id\n" +
            "            from tags t, tag_relationship r, user_files f\n" +
            "            where t.tag_id=r.tag_id and f.user_file_id=r.user_file_id and\n" +
            "                 t.deleted=false and f.deleted=false and r.deleted=false and\n" +
            "                 tag_owner=#{tagOwner} and r.user_file_id=#{userFileId}) and " +
            "tag_name like concat('%',#{tagName},'%')")
    public List<TagVO> getUnUseTags(@Param("tagOwner")Long tagOwner,
                              @Param("userFileId")Long userFileId,
                              @Param("tagName")String tagName);
}
