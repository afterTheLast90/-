package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.Files;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-27-16:20
 **/
public interface FileMapper extends BaseMapper<Files> {
    //根据md5值查找文件记录
    @Select("select * from files where file_md5=#{fileMd5} and deleted=0")
    Files findByFileMd5(String fileMd5);
    //
    @Select("select file_path from files where file_id=#{file_id} and deleted=0")
    String getByfileId(Long fielId);

    @Select("select * from files where deleted = 0  order by citations_count ")
    List<Files> getAll();

    @Select("select * from files where citations_count=0 and deleted = 0 ")
    List<Files> getAllZerroCitationsCount();
}
