package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.ReceivingRecord;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-27-16:23
 **/
public interface ReceivingRecordMapper extends BaseMapper<ReceivingRecord> {

    @Select("select * from receiving_record where inbox_id =#{inboxId} and deleted =0 ")
    public List<ReceivingRecord> getByUserId(@Param("inboxId") Long inboxId);
}
