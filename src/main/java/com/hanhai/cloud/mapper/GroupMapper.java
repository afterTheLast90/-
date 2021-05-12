package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.Group;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-27-16:21
 **/
public interface GroupMapper extends BaseMapper<Group> {
    @Select("select * from groups where group_name like concat('%',#{groupName},'%') and user_id =#{userId} and deleted = false ")
    public List<Group> getByUserIdAndGroupName(@Param("userId") Long userId, @Param("groupName")String groupName);
}
