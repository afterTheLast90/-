package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.Tag;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-27-16:24
 **/
public interface TagMapper extends BaseMapper<Tag> {
    // 根据标签名，模糊查找标签
    @Select("select * from tags where tag_name like concat('%',#{tagName},'%') and tag_owner=#{tagOwner} and deleted=false")
    public List<Tag> getByUserIdAndTagName(@Param("tagOwner")Long tagOwner, @Param("tagName")String tagName);

    // 标签名，精确查找标签
    @Select("select * from tags where tag_owner = #{tagOwner} and tag_name = #{tagName} and deleted = false")
    public Tag getTagByName(@Param("tagOwner")Long tagOwner, @Param("tagName")String tagName);

}
