package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.Files;
import org.apache.ibatis.annotations.Select;

/**
 * @author wmgx
 * @create 2021-04-27-16:20
 **/
public interface FileMapper extends BaseMapper<Files> {
    //根据md5值查找文件记录
    @Select("select * from files where file_md5=#{fileMd5}")
    Files findByFileMd5(String fileMd5);
    //
    @Select("select file_path from files where file_id=#{file_id}")
    String getByfileId(Long fielId);
}
