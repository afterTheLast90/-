package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.GroupRelationship;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author wmgx
 * @create 2021-04-27-16:21
 **/
public interface GroupRelationshipMapper extends BaseMapper<GroupRelationship> {
    @Update("update group_relationship set deleted = true where group_id = #{groupId}")
    public int delByGroupId(@Param("groupId") Long groupId);
}
