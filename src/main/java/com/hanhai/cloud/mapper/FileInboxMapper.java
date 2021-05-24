package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.FileInbox;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-27-16:20
 **/
public interface FileInboxMapper extends BaseMapper<FileInbox> {

    @Select("select * from file_inbox where publisher =#{userId} and deleted =0 ")
    public List<FileInbox> getByUserId(@Param("userId") Long userId);

    @Select("select * from file_inbox where inbox_id=#{inboxId} and deleted=0")
    public List<FileInbox> findByInboxId(@Param("inboxId") Long inboxId);
}
