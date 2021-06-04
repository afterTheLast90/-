package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.InnerShare;
import com.hanhai.cloud.vo.ShareMumbersVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * @author wmgx
 * @create 2021-04-27-16:22
 **/
public interface InnerShareMapper extends BaseMapper<InnerShare> {

    // 根据shareId，得到指定人分享的 用户列表
    @Select("select g.group_name from groups g, inner_share s\n" +
            "where s.group_id=g.group_id and\n" +
            "        s.deleted = false and g.deleted=false and\n" +
            "        share_id=#{shareId}")
    public List<String> getShardGroups(@Param("shareId")String shareId);

    // 根据shareId，得到指定人分享的 群组列表
    @Select("select u.user_name from inner_share s, user u\n" +
            "where s.user_id=u.user_id and\n" +
            "        s.deleted = false and u.deleted=false and\n" +
            "        share_id=#{shareId}")
    public List<String> getShardUsers(@Param("shareId")String shareId);


    // 得到私有分享的所有userId
    @Select("select user_id from inner_share where share_id=#{shareId} and deleted=false")
    public List<Long> getAllUserIdByShareId(@Param("shareId")String shareId);

    // 得到私有分享 所有组id里的 userId
    @Select("select distinct gr.user_id\n" +
            "from inner_share s, group_relationship gr\n" +
            "where share_id=#{shareId} and s.group_id=gr.group_id and\n" +
            "      s.deleted=false and gr.deleted=false")
    public Set<Long> getAllUserIdByGroup(@Param("shareId")String shareId);
}
