package com.hanhai.cloud.controller;

import cn.hutool.db.ds.simple.SimpleDataSource;
import com.hanhai.cloud.CloudApplication;
import com.hanhai.cloud.base.BaseException;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.configuration.SystemInfo;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.exception.UpdateException;
import com.hanhai.cloud.params.*;
import com.hanhai.cloud.service.SystemSettingService;
import com.hanhai.cloud.utils.MailUtils;
import com.hanhai.cloud.utils.SmsUtils;
import com.hanhai.cloud.utils.utils.SystemInfoRedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;
import redis.clients.jedis.Jedis;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wmgx
 * @create 2021-05-04-14:28
 **/
@Slf4j
@Controller
public class SystemSettingController {

    @Autowired
    SystemSettingService systemSettingService;

    @Autowired
    SystemInfo systemInfo;


    @Autowired
    SystemInfoRedisUtils systemInfoRedisUtils;

    @Autowired
    MailUtils mailUtils;

    @Autowired
    SmsUtils smsUtils;
    /**
     * 访问admin界面
     *
     * @return
     */
    @GetMapping("/admin")
    public String admin(Model model) {
            return "dashBoard";
    }
    /**
     * 访问install界面
     *
     * @return
     */
    @GetMapping("/install")
    public String install() {
        if (!systemInfo.getInstalled())
            return "install";
        return "redict:login";
    }

    /**
     * 获取系统目录，后期加限制，只有未安装的状态可以访问
     *
     * @param parentDirectory
     *
     * @return
     */
    @GetMapping("/system/getDir")
    @ResponseBody
    public R<List<String>> getDir(@RequestParam(value = "parentDirectory", required = false, defaultValue = "") String parentDirectory) {
        File[] files;
        // 如果传空，代表获取根目录
        if ("".equals(parentDirectory)) {
            files = File.listRoots();
        } else {
            // 只查询目录
            files = new File(parentDirectory).listFiles(File::isDirectory);
        }
        // 转字符串
        List<String> result = null;
        if (files != null) {
            result = new ArrayList<>(files.length);
            for (File file : files) {
                // 将反斜杠转换为正斜杠
                result.add(file.getPath().replaceAll("\\\\", "/"));
            }
        }
        return new R<List<String>>(ResultCode.SUCCESS_NO_SHOW).setData(result);

    }

    @PostMapping("/system/createNewFolder")
    @ResponseBody
    public R mkdir(@RequestBody @Validated CreateNewFolderParam createNewFolderParam) {
        // 父目录
        File parent = new File(createNewFolderParam.getPath());

        // 如果父目录不存在
        if (!(parent.exists() || parent.isDirectory())) {
            return new R(ResultCode.PARAMETER_ERROR).setMsg("选择的父目录不存在");
        }

        // 要创建的子目录路径
        String path = null;
        if (!createNewFolderParam.getPath().endsWith("/"))
            path = createNewFolderParam.getPath() + "/" + createNewFolderParam.getFolderName();
        else
            path = createNewFolderParam.getPath() + createNewFolderParam.getFolderName();

        File newFile = new File(path);
        // 如果有同名的文件夹或者文件
        if (newFile.exists()) {
            return new R(ResultCode.PARAMETER_ERROR).setMsg("创建的目录已经存在或存在同名文件");
        } else {
            if (newFile.mkdir()) {
                log.info("创建目录" + newFile);
                return new R(ResultCode.SUCCESS).setMsg("目录创建成功");
            } else
                return new R(ResultCode.UPDATE_ERROR).setMsg("目录创建失败，请稍后再试");
        }
    }

    @PostMapping("/system/testDB")
    @ResponseBody
    public R testDBConnection(@RequestBody @Validated DateBaseParam dateBaseParam) {

        // 测试数据库连接
        SimpleDataSource simpleDataSource = new SimpleDataSource(dateBaseParam.getJDBCUrl(), dateBaseParam.getDbUserName(),
                dateBaseParam.getDbPassword());
        try {
            simpleDataSource.getConnection();
        } catch (SQLException throwables) {
            // 如果连接失败抛异常
            return new R(ResultCode.PARAMETER_ERROR).setMsg("连接数据库失败，请检查数据库");
        }

        return new R(ResultCode.SUCCESS).setMsg("连接数据库成功");
    }

    @PostMapping("/system/install")
    @ResponseBody
    public R installSystem(@RequestBody @Validated InstallParams installParams) throws IOException, SQLException, UpdateException {

        if (systemInfo.getInstalled()) {
            return new R(ResultCode.UNAUTHORIZED_ACCESS).setMsg("系统已经安装，无需重复安装");
        }

        // 安装系统
        systemSettingService.install(installParams);

        CloudApplication.restartApplication();
        return R.getSuccess().setMsg("系统安装完成，系统自动重启");
    }

    @PostMapping("/system/testRedis")
    @ResponseBody
    public R testDBConnection(@RequestBody @Validated RedisParam redisParam) {

        try {
            Jedis jedis = new Jedis(redisParam.getRedisAddress(),redisParam.getRedisPort());
            if (!StringUtils.isEmpty(redisParam.getRedisPassword())){
                jedis.auth(redisParam.getRedisPassword());
            }
            String ping = jedis.ping();
            if (ping.equalsIgnoreCase("PONG")) {
                // 释放连接资源
                jedis.close();
                return new R(ResultCode.SUCCESS).setMsg("连接Redis成功");
            }else
                return new R(ResultCode.PARAMETER_ERROR).setMsg("连接Redis失败，请检查参数");
        } catch (Exception exception) {
            // 如果连接失败抛异常
            return new R(ResultCode.PARAMETER_ERROR).setMsg("连接Redis失败，请检查参数");
        }

    }

    @GetMapping("/settings")
    public String settings(Model model ){
        model.addAttribute("systemInfo",systemInfo);
        return "systemSettings";
    }

    @PostMapping("/admin/openRegistration")
    @ResponseBody
    public R openRegistration(@RequestBody Map<String, String> status) throws BaseException{
        if (!status.containsKey("status"))
            throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("参数缺失");

        boolean s = Boolean.valueOf(status.get("status"));
        // 如果是开启
        if (s){
            if (systemInfo.getAlySmsEnabled() && systemInfo.getEmailEnabled()){
                systemSettingService.updateById("open_registration",true);
                systemInfo.setOpenRegistration(true);
            }else{
                throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("须先开启短信服务与smtp服务");
            }
        }else{
            systemSettingService.updateById("open_registration",false);
            systemInfo.setOpenRegistration(false);
        }
        return R.getSuccess().setMsg(s?"开启注册服务成功":"关闭注册服务成功");
    }

    @PostMapping("/admin/emailEnabled/close")
    @ResponseBody
    public  R closeEmail(){
        systemSettingService.updateById("email_enabled",false);
        systemInfo.setEmailEnabled(false);
//        if (!systemInfo.getAlySmsEnabled()){
            systemSettingService.updateById("open_registration",false);
            systemInfo.setOpenRegistration(false);
//        }
        return R.getSuccess().setMsg("Email服务已关闭").setData(systemInfo.getAlySmsEnabled());
    }


    @PostMapping("/admin/sms/close")
    @ResponseBody
    public  R closeSms(){
        systemSettingService.updateById("aly_sms_enabled",false);
        systemInfo.setAlySmsEnabled(false);
//        if (!systemInfo.getEmailEnabled()){
            systemSettingService.updateById("open_registration",false);
            systemInfo.setOpenRegistration(false);
//        }
        return R.getSuccess().setMsg("阿里云短信服务已关闭").setData(systemInfo.getEmailEnabled());
    }

    @PostMapping("/admin/email/test")
    @ResponseBody
    public  R testEmail(@RequestBody @Validated SmtpTestParams smtpTestParams) throws UnsupportedEncodingException, MessagingException {
        mailUtils.send(smtpTestParams);
        return R.getSuccess().setMsg("发送成功");
    }


    @PostMapping("/admin/email/setSetting")
    @ResponseBody
    public  R configEmail(@RequestBody @Validated SmtpParams smtpParams) throws UnsupportedEncodingException, MessagingException {
        systemInfo.setSmtpServer(smtpParams.getSmtpServer());
        systemInfo.setSmtpUsername(smtpParams.getSmtpUsername());
        systemInfo.setSmtpPassword(smtpParams.getSmtpPassword());
        systemInfo.setSmtpPort(smtpParams.getSmtpPort());
        systemInfo.setSmtpSender(smtpParams.getSmtpSender());
        systemInfo.setEmailEnabled(true);

        systemSettingService.updateById("email_enabled",true);
        systemSettingService.updateById("smtp_server",systemInfo.getSmtpServer());
        systemSettingService.updateById("smtp_port",systemInfo.getSmtpPort());
        systemSettingService.updateById("smtp_username",systemInfo.getSmtpUsername());
        systemSettingService.updateById("smtp_password",systemInfo.getSmtpPassword());
        systemSettingService.updateById("smtp_sender",systemInfo.getSmtpSender());

        mailUtils.updateInfo();
//        mailUtils.send("1109270538@qq.com","你好","123");
        return R.getSuccess().setMsg("设置成功");
    }

    @PostMapping("/admin/sms/test")
    @ResponseBody
    public  R testEmail(@RequestBody @Validated AlySmsTestParams alySmsTestParams){
        smsUtils.sendTest(alySmsTestParams);
        return R.getSuccess().setMsg("发送成功");
    }


    @PostMapping("/admin/sms/config")
    @ResponseBody
    public  R configSms(@RequestBody @Validated AlySmsParams alySmsParams) throws UnsupportedEncodingException,
            MessagingException {

        systemInfo.setAlySmsEnabled(true);
        systemInfo.setAlySmsSecret(alySmsParams.getAlySmsSecret());
        systemInfo.setAlySmsRegionId(alySmsParams.getAlySmsRegionId());
        systemInfo.setAlySmsTemplateCode(alySmsParams.getAlyTemplateCode());
        systemInfo.setAlySmsAccessKeyId(alySmsParams.getAlySmsAccessKeyId());

        systemSettingService.updateById("aly_sms_enabled",true);
        systemSettingService.updateById("aly_sms_region_id",systemInfo.getAlySmsRegionId());
        systemSettingService.updateById("aly_sms_access_key_id",systemInfo.getAlySmsAccessKeyId());
        systemSettingService.updateById("aly_sms_secret",systemInfo.getAlySmsSecret());
        systemSettingService.updateById("aly_sms_template_code",systemInfo.getAlySmsTemplateCode());

        smsUtils.update();
//        smsUtils.send("15130861120");
        return R.getSuccess().setMsg("设置成功");
    }

    @PostMapping("/admin/webInfo/config")
    @ResponseBody
    public R webInfoConfig(@Validated @RequestBody WebInfoParams webInfoParams){
        if(!webInfoParams.getSiteUrl().endsWith("/"))
            webInfoParams.setSiteUrl(webInfoParams.getSiteUrl()+"/");
        systemInfo.setSiteUrl(webInfoParams.getSiteUrl());
        systemInfo.setSiteIcp(webInfoParams.getSiteIcp());
        systemInfo.setSiteName(webInfoParams.getSiteName());

        systemSettingService.updateById("site_name",systemInfo.getSiteName());
        systemSettingService.updateById("site_url",systemInfo.getSiteUrl());
        systemSettingService.updateById("site_icp",systemInfo.getSiteIcp());


        return R.getSuccess().setMsg("设置成功");


    }

}


