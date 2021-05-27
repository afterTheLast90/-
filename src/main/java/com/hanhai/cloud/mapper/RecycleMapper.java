package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.Recycle;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-27-16:23
 **/
public interface RecycleMapper extends BaseMapper<Recycle> {
    @Select("select * from recycle where user_id=#{userId}")
    public List<Recycle> getRecycleFile(@Param("userId") Long userId);
}
