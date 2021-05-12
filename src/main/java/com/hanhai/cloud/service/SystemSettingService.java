package com.hanhai.cloud.service;

import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.hutool.db.sql.SqlExecutor;
import com.github.yitter.idgen.YitIdHelper;
import com.hanhai.cloud.base.BaseService;
import com.hanhai.cloud.entity.SystemSetting;
import com.hanhai.cloud.exception.UpdateException;
import com.hanhai.cloud.params.InstallParams;
import com.hanhai.cloud.utils.PasswordEncryptionUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * @author wmgx
 * @create 2021-05-04-10:37
 **/
@Service
public class SystemSettingService extends BaseService {
    public String selectById(String key) {
        SystemSetting setting = systemSettingsMapper.selectById(key);
        return setting == null ? "" : setting.getSettingValue();
    }

    /**
     * 安装系统
     *
     * @param installParams
     *         安装参数
     *
     * @throws IOException
     * @throws SQLException
     *         sql错误
     * @throws UpdateException
     *         安装失败
     */
    public void install(InstallParams installParams) throws IOException, SQLException, UpdateException {


//        ApplicationHome h = new ApplicationHome(getClass());
//        String profilepath = h.getSource().getParentFile()
//                .toString().replaceAll("\\\\","/")+"/application.properties";

        FileSystemResource application = new FileSystemResource("application.properties");
        File file = application.getFile();

        if (file.exists() && file.delete())
            throw new UpdateException().setMsg("删除配置文件失败，请检测权限");

        if (!file.createNewFile())
            throw new UpdateException().setMsg("创建配置文件失败，请检测权限");

        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8")));

        // 保存设置
        out.println("spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver");
        out.println("spring.datasource.name=defaultDataSource");
        out.println("spring.datasource.url=" + installParams.getJDBCUrl());
        out.println("spring.datasource.username=" + installParams.getDbUserName());
        out.println("spring.datasource.password=" + installParams.getDbPassword());
        out.println("system.info.installed=true");
        out.close();

        // 建表
        SimpleDataSource simpleDataSource = new SimpleDataSource(installParams.getJDBCUrl(), installParams.getDbUserName(),
                installParams.getDbPassword());
        Connection connection = simpleDataSource.getConnection();

        ClassPathResource rc = new ClassPathResource("sql/cloud.sql");
        EncodedResource er = new EncodedResource(rc, "utf-8");
        ScriptUtils.executeSqlScript(connection, er);
        // 插入数据
        SqlExecutor.execute(connection, "INSERT INTO `user` (`user_id`, `user_name`, " +
                        "`user_password`, `user_avatar`, `user_gender`, `user_email`, " +
                        "`user_phone`, `email_checked`, `phone_checked`, `space_size`, `used_size`, `admin`, `created_time`, `deleted`, `updated_time`) VALUES\n" +
                        "(?, ?, ?, NULL, 0, ?, ?, '0', '0', 100000, 0, '1'," +
                        " ?, '0', ?)", YitIdHelper.nextId(), installParams.getUserName(),
                PasswordEncryptionUtils.hashPassword(installParams.getPassword()),
                installParams.getEmail(), installParams.getPhoneNumber(), LocalDateTime.now(),
                LocalDateTime.now());

        if (!installParams.getSiteUrl().endsWith("/"))
            installParams.setSiteUrl(installParams.getSiteUrl() + "/");

        insertSystemSetting(connection, "file_upload_dir", installParams.getFileUploadPath());
        insertSystemSetting(connection, "site_name", installParams.getSiteName());
        insertSystemSetting(connection, "site_url", installParams.getSiteUrl());
        insertSystemSetting(connection, "site_icp", installParams.getIcp());

        // 是否开放注册
        insertSystemSetting(connection, "open_registration", "false");
        // 邮件相关的
        insertSystemSetting(connection, "email_enabled", "false");
        insertSystemSetting(connection, "smtp_server", "");
        insertSystemSetting(connection, "smtp_port", "25");
        insertSystemSetting(connection, "smtp_username", "");
        insertSystemSetting(connection, "smtp_password", "");
        insertSystemSetting(connection, "smtp_sender", "");


        // 手机短信相关的
        insertSystemSetting(connection, "aly_sms_enabled", "false");
        insertSystemSetting(connection, "aly_sms_region_id", "");
        insertSystemSetting(connection, "aly_sms_access_key_id", "");
        insertSystemSetting(connection, "aly_sms_secret", "");


    }

    /**
     * 插入系统设置项
     *
     * @param connection
     *         数据库连接
     * @param key
     *         key
     * @param value
     *         value
     *
     * @throws SQLException
     */
    private void insertSystemSetting(Connection connection, String key, String value) throws SQLException {
        SqlExecutor.execute(connection,
                "INSERT INTO `system_settings` (`setting_key`, `setting_value`," +
                        " `setting_comment`, `deleted`, `updated_time`) VALUES " +
                        "(?, ?, '',  '0', ?)", key, value, LocalDateTime.now());
    }
}
