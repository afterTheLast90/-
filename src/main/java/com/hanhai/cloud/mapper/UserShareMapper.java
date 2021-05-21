package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.UserShare;
import com.hanhai.cloud.vo.ResourceVO;
import com.hanhai.cloud.vo.UserShareVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wmgx
 * @create 2021-04-27-16:25
 **/
public interface UserShareMapper extends BaseMapper<UserShare> {
    // 未登录公共资源（公共）（下载）
    @Select("select share_id, file_name, user_name, u.created_time as created_time, expire_time " +
            "from user_share s, user u, user_files f " +
            "where s.user_id=u.user_id and s.user_file_id=f.user_file_id and " +
            "u.deleted=false and s.deleted=false and f.deleted=false and " +
            "(download_times<max_download_times or max_download_times=-1) and " +
            "(expire_time>#{nowTime} or expire_time='1970-01-01T07:59:59') and " +
            "share_type=0 and " +
            "file_name like concat('%',#{fileName},'%')")
    public List<ResourceVO> getPublicShare(@Param("nowTime") LocalDateTime nowTime,@Param("fileName") String fileName);

    // 登录后的公共资源（公共 + 内部）（下载 + 上传）
    @Select("select share_id, file_name, user_name, u.created_time as created_time, expire_time, max_download_times, max_file_dump_times, download_times, file_dump_time " +
            "from user_share s, user u, user_files f " +
            "where s.user_id=u.user_id and s.user_file_id=f.user_file_id and " +
            "u.deleted=false and s.deleted=false and f.deleted=false and " +
            "(download_times<max_download_times or file_dump_time<max_file_dump_times or max_download_times=-1 or max_file_dump_times=-1) and " +
            "(expire_time>#{nowTime} or expire_time='1970-01-01T07:59:59') and " +
            "share_type in (0, 1) and " +
            "file_name like concat('%',#{fileName},'%')")
    public List<ResourceVO> getUserPublicShare(@Param("nowTime") LocalDateTime nowTime,@Param("fileName") String fileName);

    // 用户所有分享信息(文件被删除，也会显示分享纪录）
    @Select("select s.*, file_name " +
            "from user_files f, user_share s " +
            "where f.user_file_id=s.user_file_id and " +
            "s.deleted=false and s.user_id = #{userId} and " +
            "file_name like concat('%',#{fileName},'%')")
    public List<UserShareVO> getUserShare(@Param("userId")Long userId, @Param("fileName")String fileName);

    @Update("update user_share set expire_time=#{nowTime} where share_id=#{shareId}")
    public void closeShare(@Param("nowTime")LocalDateTime nowTime, @Param("shareId")Long shareId);
}
