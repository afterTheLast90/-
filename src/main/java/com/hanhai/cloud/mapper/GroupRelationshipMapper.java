package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.GroupRelationship;
import com.hanhai.cloud.vo.GroupRelationVO;
import com.hanhai.cloud.vo.GroupUserSearchVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-27-16:21
 **/
public interface GroupRelationshipMapper extends BaseMapper<GroupRelationship> {
    // 删除组时，级联删除 与用户的关系纪录
    @Update("update group_relationship set deleted = true where group_id = #{groupId}")
    public int delByGroupId(@Param("groupId") Long groupId);

    // 根据组id，得到用户列表
    @Select("select r.*, user_name, user_avatar " +
            "from user u, groups g, group_relationship r where " +
            "u.user_id=r.user_id and g.group_id=r.group_id and " +
            "u.deleted=false and g.deleted=false and r.deleted=false and " +
            "r.group_id=#{groupId}")
    public List<GroupRelationVO> getGroupRelation(@Param("groupId")Long groupId);

    // 根据用户名，搜索前十个用户(不包括组内已有用户，和用户本身）
    @Select("select user_id, user_name, user_phone, user_email from user where " +
            "(user_name like concat('%',#{userName},'%') or " +
                "user_email like concat('%',#{userName},'%') or " +
                "user_phone like concat('%',#{userName},'%')) and " +
            "user_id not in " +
            "(select user_id from group_relationship g where g.group_id=#{groupId} and deleted=false) and " +
            "user_id!=#{userId} and deleted=false limit 10")
    public List<GroupUserSearchVO> searchUserByName(@Param("groupId")Long groupId,
                                                    @Param("userName")String userName,
                                                    @Param("userId")Long userId);

    // 新建群组时，搜索用户
    @Select("select user_id, user_name, user_phone, user_email from user where " +
            "(user_name like concat('%',#{userName},'%') or " +
            "user_email like concat('%',#{userName},'%') or " +
            "user_phone like concat('%',#{userName},'%')) and " +
            "user_id!=#{userId} and deleted=false limit 10")
    public List<GroupUserSearchVO> getUsersByName(@Param("userName")String userName,
                                                  @Param("userId")Long userId);
}
