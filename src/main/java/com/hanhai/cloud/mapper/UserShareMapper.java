package com.hanhai.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanhai.cloud.entity.User;
import com.hanhai.cloud.entity.UserFile;
import com.hanhai.cloud.entity.UserShare;
import com.hanhai.cloud.vo.GetShareVO;
import com.hanhai.cloud.vo.ResourceVO;
import com.hanhai.cloud.vo.UserShareVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author wmgx
 * @create 2021-04-27-16:25
 **/
public interface UserShareMapper extends BaseMapper<UserShare> {
    // 未登录公共资源（公共）（下载）
    @Select("select share_id, file_name, file_type, f.user_file_id as user_file_id, user_name, s.created_time as created_time, expire_time " +
            "from user_share s, user u, user_files f " +
            "where s.user_id=u.user_id and s.user_file_id=f.user_file_id and " +
            "u.deleted=false and s.deleted=false and f.deleted=false and " +
            "(download_times<max_download_times or max_download_times=-1) and " +
            "(expire_time>#{nowTime} or expire_time='1970-01-01T07:59:59') and " +
            "share_type=0 and " +
            "file_name like concat('%',#{fileName},'%') order by s.created_time desc")
    public List<ResourceVO> getPublicShare(@Param("nowTime") LocalDateTime nowTime,@Param("fileName") String fileName);

    // 登录后的公共资源（公共 + 内部）（下载 + 上传）
    @Select("select share_id, file_name, file_type, f.user_file_id as user_file_id, user_name, s.created_time as created_time, expire_time, max_download_times, max_file_dump_times, download_times, file_dump_time " +
            "from user_share s, user u, user_files f " +
            "where s.user_id=u.user_id and s.user_file_id=f.user_file_id and " +
            "u.deleted=false and s.deleted=false and f.deleted=false and " +
            "(download_times<max_download_times or file_dump_time<max_file_dump_times or max_download_times=-1 or max_file_dump_times=-1) and " +
            "(expire_time>#{nowTime} or expire_time='1970-01-01T07:59:59') and " +
            "share_type in (0, 1) and " +
            "file_name like concat('%',#{fileName},'%') order by s.created_time desc")
    public List<ResourceVO> getUserPublicShare(@Param("nowTime") LocalDateTime nowTime,@Param("fileName") String fileName);

    // 得到用户所有分享信息(文件被删除，不会显示分享纪录）
    @Select("select s.*, file_name, file_type, file_parent_path, f.user_file_id as user_file_id " +
            "from user_files f, user_share s " +
            "where f.user_file_id=s.user_file_id and " +
            "s.deleted=false and f.deleted=false and s.user_id = #{userId} and " +
            "file_name like concat('%',#{fileName},'%') order by s.created_time desc")
    public List<UserShareVO> getUserShare(@Param("userId")Long userId, @Param("fileName")String fileName);

    // 取消分享
    @Update("update user_share set expire_time=#{nowTime} where share_id=#{shareId}")
    public void closeShare(@Param("nowTime")LocalDateTime nowTime, @Param("shareId")String shareId);

    // 分享详情
    @Select("select s.*, file_name, file_type " +
            "from user_files f, user_share s " +
            "where f.user_file_id=s.user_file_id and " +
            "s.deleted=false and s.share_id = #{shareId}")
    public UserShareVO getShareDetail(@Param("shareId")String shareId);

    // 获取分享文件(不可获取用户已删除文件的分享)
    @Select("select s.*, file_name, file_type, file_size " +
            "from user_files f, user_share s " +
            "where f.user_file_id=s.user_file_id and " +
            "s.deleted=false and f.deleted=false and s.share_id=#{shareId}")
    public GetShareVO getShare(@Param("shareId")String shareId);

    // 获取分享文件夹的内容
    @Select("select user_file_id, file_name, file_type, file_size " +
            "from user_files " +
            "where file_parent_path=#{path} and deleted=false")
    public List<GetShareVO> getShareByFolder(@Param("path")String path);


    // 根据shareId得到密码
    @Select("select share_password from user_share where share_id=#{shareId}")
    public String getPwdByShareId(@Param("shareId")String shareId);

    // 根据shareId,得到父目录路径
    @Select("select file_parent_path from user_files f, user_share s " +
            "where s.user_file_id=f.user_file_id and share_id=#{shareId}")
    public String getParentPathByShareId(@Param("shareId")String shareId);

    // 根据filePath,得到子目录文件
    @Select("select file_name, file_type, user_file_id\n" +
            "from user_files f\n" +
            "where file_parent_path=#{path} and deleted=false and " +
            "file_name like concat('%',#{fileName},'%')")
    public List<ResourceVO> getFileByPath(@Param("path")String path, @Param("fileName")String fileName);

    // 根据shareId,得到用户信息
    @Select("select u.* " +
            "from user u, user_share s " +
            "where u.user_id=s.user_id and " +
            "u.deleted=false and s.deleted=false and " +
            "s.share_id=#{shareId}")
    public User getUserByShareId(@Param("shareId")String shareId);

    // 根据shareId,得到对应的类型
    @Select("select file_type\n" +
            "from user_files f, user_share s\n" +
            "where f.user_file_id=s.user_file_id and\n" +
            "      share_id=#{shareId}")
    public String getFileTypeByShareId(@Param("shareId")String shareId);

    // 由userFileId数组，得到所对应的文件
    public List<UserFile> getFileByUFIds(@Param("userFileIds")Long[] userFileIds);

    // 根据文件父目录，得到该目录所有文件（需要注意：以根目录寻找，会得到所有用户根目录文件）
    @Select("select * from user_files where file_parent_path=#{fileParentPath} and deleted=0")
    public List<UserFile> getFileByParentPath(@Param("fileParentPath")String fileParentPath);

    // 根据 用户的 文件父目录，得到用户该目录所有文件
    @Select("select * from user_files where file_parent_path=#{fileParentPath} and user_id=#{userId} and deleted=0")
    public List<UserFile> getFileByParentPathAndUser(@Param("userId")Long userId, @Param("fileParentPath")String fileParentPath);

    // 分享信息的 转存次数+1
    @Update("update user_share set file_dump_time=file_dump_time+1 where share_id=#{shareId}")
    public void addDumpTime(@Param("shareId")String shareId);

//    // 根据userFileId 和 shareId 得到文件（用于下载分享文件 检验）
//    @Select("select f.* from user_files f, user_share s\n" +
//            "where f.user_file_id=s.user_file_id and\n" +
//            "      f.user_file_id=#{userFileId} and share_id=#{shareId} and\n" +
//            "      f.deleted=0 and s.deleted=0")
//    public List<UserFile> getFileByFileIdAndShareId(@Param("userFileId") Long userFileId, @Param("shareId") String shareId);

    // 分享信息的 下载次数+1
    @Update("update user_share set download_times=download_times+1 where share_id=#{shareId}")
    public void addDownTime(@Param("shareId")String shareId);

//    // 根据userFileId 得到其父级目录
//    @Select("select file_parent_path from user_files where user_file_id=#{userFileId} and deleted=0")
//    public String getPathByUserFileId(@Param("userFileId")Long userFileId);
//
//    // 根据shareId 得到分享文件所在目录
//    @Select("select file_parent_path from user_files f, user_share s\n" +
//            "where f.user_file_id=s.user_file_id and file_type='DIR' and\n" +
//            "      share_id=#{shareId} and f.deleted=0 and s.deleted=0")
//    public String getPathByShareId(@Param("shareId")String shareId);
}
