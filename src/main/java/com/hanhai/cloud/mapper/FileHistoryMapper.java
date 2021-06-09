package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.FileHistory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-27-16:20
 **/
public interface FileHistoryMapper extends BaseMapper<FileHistory> {
    @Select("select * from file_history where user_file_id=#{userFileId} and deleted=0")
    public List<FileHistory> getFileHistory(@Param("userFileId") Long userFileId);
}
