package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.UserFile;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-27-16:25
 **/
public interface UserFileMapper extends BaseMapper<UserFile> {
    @Select("select * from user_files where file_parent_path=#{path} and user_id=#{userId} and deleted = 0 ")
    public List<UserFile> getFilesByParent(@Param("path") String path,@Param("userId")  Long userId);

    @Select("select * from user_files where file_parent_path=#{path} and user_id=#{userId} and file_type='DIR' and deleted=0 order by updated_time")
    public List<UserFile> getDir(@Param("path") String path,@Param("userId") Long userId);

    @Select("select * from user_files where file_parent_path=#{path} and user_id=#{userId} and file_type!='DIR' and deleted=0 order by updated_time")
    public List<UserFile> getFiles(@Param("path") String path,@Param("userId") Long userId);

    @Select("select * from user_files where file_name=#{name} and user_id=#{userId} and file_type='DIR' and deleted = 0 ")
    public List<UserFile> getDirByName(@Param("name") String name,@Param("userId")  Long userId);

}
