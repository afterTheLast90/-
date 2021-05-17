package com.hanhai.cloud.configuration;

import com.hanhai.cloud.service.SystemSettingService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.StandardEnvironment;

import javax.annotation.PostConstruct;

/**
 * @author wmgx
 * @create 2021-04-27-20:32
 **/
@Configuration
@Data
@Lazy
public class SystemInfo {

    @Autowired
    SystemSettingService systemSettingService;

    @Value("${system.info.installed}")
    private Boolean installed = Boolean.FALSE;


    /**
     * 上传路径
     */
    private String upLoadPath;

    /**
     * 站点名称
     */
    private String siteName;
    /**
     * 站点url
     */
    private String siteUrl;
    /**
     * 站点备案号
     */
    private String siteIcp;
    /**
     * 是否开放注册
     */
    private Boolean openRegistration;

    /**
     * 是否开启email服务
     */
    private Boolean emailEnabled;
    /**
     * smtp服务器
     */
    private String smtpServer;
    /**
     * smtp端口号
     */
    private Integer smtpPort;
    /**
     * smtp用户名
     */
    private String smtpUsername;
    /**
     * smtp密码
     */
    private String smtpPassword;
    /**
     * smtp发送人名
     */
    private String smtpSender;


    /**
     * 阿里云短息服务是否开启
     */
    private Boolean alySmsEnabled;
    /**
     * 阿里云短息服务地区id
     */
    private String alySmsRegionId;
    /**
     * 阿里云短息服务访问key
     */
    private String alySmsAccessKeyId;
    /**
     * 阿里云短信服务访问密码
     */
    private String alySmsSecret;

    @PostConstruct
    public void initSystemConfig() {
        // 如果没有安装，不注入系统设置
        if (!installed)
            return;


        this.upLoadPath = systemSettingService.selectById("file_upload_dir");
        this.siteName=systemSettingService.selectById("site_name");
        this.siteUrl=systemSettingService.selectById("site_url");
        this.siteIcp=systemSettingService.selectById("site_icp");

        this.openRegistration= Boolean.valueOf(systemSettingService.selectById("open_registration"));

        this.emailEnabled= Boolean.valueOf(systemSettingService.selectById("email_enabled"));
        this.smtpServer=systemSettingService.selectById("smtp_server");
        this.smtpPort=Integer.parseInt(systemSettingService.selectById("smtp_port"));
        this.smtpUsername=systemSettingService.selectById("smtp_username");
        this.smtpPassword=systemSettingService.selectById("smtp_password");
        this.smtpSender=systemSettingService.selectById("smtp_sender");

        this.alySmsEnabled= Boolean.valueOf(systemSettingService.selectById("aly_sms_enabled"));
        this.alySmsRegionId=systemSettingService.selectById("aly_sms_region_id");
        this.alySmsAccessKeyId=systemSettingService.selectById("aly_sms_access_key_id");
        this.alySmsSecret=systemSettingService.selectById("aly_sms_secret");


    }

}
