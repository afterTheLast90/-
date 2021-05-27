package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.InnerShare;
import com.hanhai.cloud.vo.ShareMumbersVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
    public List<String> getShardUsers(@Param("shareId")Long shareId);

    // 根据shareId，得到指定人分享的 群组列表
    @Select("select u.user_name from inner_share s, user u\n" +
            "where s.user_id=u.user_id and\n" +
            "        s.deleted = false and u.deleted=false and\n" +
            "        share_id=#{shareId}")
    public List<String> getShardGroups(@Param("shareId")Long shareId);
}
