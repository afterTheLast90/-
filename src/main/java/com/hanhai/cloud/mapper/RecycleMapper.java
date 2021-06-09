package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.Recycle;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-27-16:23
 **/
public interface RecycleMapper extends BaseMapper<Recycle> {
    @Select("select * from recycle where recycle_id in (select recycle_id from user_files where user_id=#{userId} and deleted=1) and deleted=0 and datediff(now(),created_time) between 0 and 30 order by created_time")
    public List<Recycle> getRecycleFile(@Param("userId")  Long userId);

    public List<Recycle> getByIds(@Param("ids") Long[] ids);

    @Select("select * from recycle where recycle_id=#{recycleId}")
    public Recycle getById(@Param("recycleId") Long id);

    @Select("select * from recycle where deleted=0")
    public List<Recycle> getRecycleAllFiles();

    @Update("update recycle set deleted=#{recycleFile.deleted} where recycle_id=#{recycleFile.recycleId}")
    public Integer reductionById(@Param("recycleFile") Recycle recycleFile);
}
