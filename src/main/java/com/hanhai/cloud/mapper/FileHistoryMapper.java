package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.dto.RecycleSum;
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

    @Select("select file_history.file_name, file_history.file_id,user_id,count(*) as cou,sum" +
            "(file_history.file_size) as sum from file_history join            user_files on file_history.deleted = 0 and file_history.user_file_id = user_files.user_file_id     and user_files.deleted=1 and DATE_SUB(CURDATE(), INTERVAL 30 DAY) >        date(file_history.updated_time) GROUP by file_history.file_id ")
    public List<RecycleSum> getNotDelete();
}
