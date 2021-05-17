package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.Group;
import com.hanhai.cloud.params.UpdGroupParams;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-27-16:21
 **/
public interface GroupMapper extends BaseMapper<Group> {
    @Select("select * from groups where group_name like concat('%',#{groupName},'%') and user_id =#{userId} and deleted = false ")
    public List<Group> getByUserIdAndGroupName(@Param("userId") Long userId, @Param("groupName")String groupName);

    @Update("update groups set group_name = #{group.groupName} where group_id = #{group.groupId}")
    public int updGroupName(@Param("group")Group group);

    @Select("select * from groups where user_id = #{userId} and group_name = #{groupName} and deleted = false")
    public Group getGroupByName(@Param("groupName") String groupName,
                              @Param("userId") Long userId);
}
