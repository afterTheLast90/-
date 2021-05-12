package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.hutool.db.sql.SqlExecutor;
import com.github.yitter.idgen.YitIdHelper;
import com.hanhai.cloud.CloudApplication;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.configuration.SystemInfo;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.exception.UpdateException;
import com.hanhai.cloud.params.CreateNewFolderParam;
import com.hanhai.cloud.params.DateBaseParam;
import com.hanhai.cloud.params.InstallParams;
import com.hanhai.cloud.service.SystemSettingService;
import com.hanhai.cloud.utils.PasswordEncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public R installSystem(@RequestBody @Validated InstallParams installParams) throws IOException, SQLException,UpdateException {

        if (systemInfo.getInstalled()){
            return new R(ResultCode.UNAUTHORIZED_ACCESS).setMsg("系统已经安装，无需重复安装");
        }

        // 安装系统
        systemSettingService.install(installParams);

        CloudApplication.restartApplication();
        return  R.getSuccess().setMsg("系统安装完成，系统自动重启");
    }

}


