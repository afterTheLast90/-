package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.ReceivingRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-27-16:23
 **/
public interface ReceivingRecordMapper extends BaseMapper<ReceivingRecord> {

    @Select("select * from receiving_record where inbox_id =#{inboxId} and deleted =0 ")
    public List<ReceivingRecord> getByUserId(@Param("inboxId") Long inboxId);

    @Select("select * from receiving_record where inbox_id =#{inboxId} and input_name =#{inputName} and deleted =0 ")
    public List<ReceivingRecord> getByName(@Param("inboxId") Long inboxId,@Param("inputName") String inputName);
}
