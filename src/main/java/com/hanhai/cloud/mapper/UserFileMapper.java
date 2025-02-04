package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.dto.RecycleSum;
import com.hanhai.cloud.entity.FileHistory;
import com.hanhai.cloud.entity.Files;
import com.hanhai.cloud.entity.UserFile;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-27-16:25
 **/
public interface UserFileMapper extends BaseMapper<UserFile> {
    @Select("select * from user_files where file_parent_path=#{path} and user_id=#{userId} and deleted = 0 ")
    public List<UserFile> getFilesByParent(@Param("path") String path,@Param("userId")  Long userId);

    @Select("select * from user_files where file_parent_path=#{path} and user_id=#{userId} and " +
            " file_type='DIR' and deleted=0 order by updated_time desc")
    public List<UserFile> getDir(@Param("path") String path,@Param("userId") Long userId);

    @Select("select * from user_files where file_parent_path=#{path} and user_id=#{userId} and " +
            "file_type!='DIR' and deleted=0 order by updated_time desc")
    public List<UserFile> getFiles(@Param("path") String path,@Param("userId") Long userId);

    @Select("select * from user_files where file_parent_path=#{path} and file_name=#{name} and user_id=#{userId} and file_type=#{type} and deleted = 0 ")
    public List<UserFile> getFileByNameAndType(@Param("path") String path,@Param("name") String name,@Param("type")String type,@Param("userId")  Long userId);

    @Select("select * from user_files where file_parent_path=#{path} and file_name=#{name} and user_id=#{userId} and deleted = 0 ")
    public List<UserFile> getDirByName(@Param("path") String path,@Param("name") String name,@Param("userId")  Long userId);

    public List<UserFile> getByIds(@Param("ids") Long[] ids,@Param("userId")  Long userId);

    @Select("select * from user_files where user_file_id=#{userFileId} and deleted=0 and user_id=#{userId}")
    public List<UserFile> getFileById(@Param("userFileId") Long userFileId,@Param("userId") Long userId);

    // 根据userFileId，得到用户文件信息
    @Select("select * from user_files where user_file_id=#{userFileId}")
    public UserFile getUserFileById(@Param("userFileId")Long userFileId);

    @Select("select * from user_files where find_in_set(#{name},file_name) and user_id=#{userId} and  deleted=0 order by updated_time")
    public List<UserFile> getByName(@Param("name")String name,@Param("userId") Long usrId);

//    @Select("select * from file_history where user_file_id=#{userFileId}")
//    public List<FileHistory> getFileHistory(@Param("userFileId") Long userFileId);
    @Select("select * from user_files where recycle_id =#{recycleId} and user_id=#{userId} order by created_time limit 1")
    public UserFile getByRecycleIdTopOne(@Param("recycleId")Long recycleId,@Param("userId")Long userId);

    @Select("select * from user_files where recycle_id =#{recycleId} and user_id=#{userId} and file_parent_path=#{path} ")
    public List<UserFile> getByRecycleIdAndParentPath(@Param("recycleId")Long recycleId,@Param("path")String path,@Param("userId")Long userId);

//    @Update("update user_files set file_name=#{fileName},file_parent_path=#{fileParentPath},recycle_id=#{recycleId,created_time=#{createdTime},deleted=#{deleted} where user_file_id=#{userFileId}")
//    public Integer reductionById(@Param("fileName") String fileName, @Param("fileParentPath") String fileParentPath,
//                                 @Param("recycleId") Long recycleId, @Param("createdTime") LocalDateTime createdTime,
//                                 @Param("deleted") Boolean deleted,@Param("userId") Long userId);
//

    @Update("update user_files set file_name =#{file.fileName},file_parent_path=#{file.fileParentPath},recycle_id=#{file.recycleId},created_time=#{file.createdTime},deleted=#{file.deleted} where user_file_id=#{file.userFileId} and user_id=#{userId}")
    public Integer reductionById(@Param("file") UserFile file,@Param("userId") Long userId);

    @Select("select * from user_files where file_parent_path=#{path} and user_id=#{userId} and deleted = 1 ")
    public List<UserFile> getReductionFilesByParent(@Param("path") String path,@Param("userId")  Long userId);

    @Select("select file_type from user_files where user_file_id =#{userFileId} and user_id = #{userId}   and deleted = 0 ")
    public String getFileTypeByUserFileId(@Param("userFileId") Long userFileId,@Param("userId") Long userId);
    @Select("select file_id from user_files where user_file_id =#{userFileId} and user_id = #{userId}   and deleted = 0 ")
    public Long getFileIdByUserFileId(@Param("userFileId") Long userFileId,@Param("userId") Long userId);



    @Select("select * from user_files where file_parent_path=#{path} and file_name=#{name} and user_id=#{userId} and deleted = 0 ")
    public List<UserFile> getByName(@Param("path") String path,@Param("name") String name,@Param("userId")  Long userId);


    public List<UserFile> getByNames(@Param("path") String path ,@Param("names") String[] names, @Param("userId") Long userId);

    @Select("select * from files where file_id=(select file_id from user_files where recycle_id =#{recycleId} and user_id=#{userId} order by created_time limit 1)")
    public Files getFilesByRecycleId(@Param("recycleId")Long recycleId,@Param("userId")Long userId);

    @Update("update files set citations_count=#{file.citationsCount} where file_id=#{file.fileId}")
    public Integer updateFiles(@Param("file") Files file);

    @Select("select * from file_history where history_id=#{historyId}")
    public FileHistory getFileHistoryById(@Param("historyId") Long historyId);

    // 根据文件名，搜索当前目录下所有文件
    @Select("select * from user_files " +
            "where file_parent_path like concat(#{parentPath},'%') and file_name like concat('%',#{fileName},'%') and " +
            "user_id=#{userId} and deleted=0 order by updated_time desc")
    public List<UserFile> getFileByNameAndPath(@Param("parentPath")String parentPath, @Param("fileName")String fileName, @Param("userId")Long userId);

    @Select("select file_id ,sum(file_size) as sum , user_id,count(*) as cou from user_files " +
            "where recycle_id = #{recycleId} and file_type !='DIR' group by file_id ")
    public List<RecycleSum> getFileIdSizeUserId(@Param("recycleId") Long recycleId);
}