package com.hanhai.cloud.utils.utils;

import com.hanhai.cloud.configuration.SystemInfo;
import com.hanhai.cloud.utils.BaseFileUtil;
import org.springframework.stereotype.Component;

@Component
public class FileUtils extends BaseFileUtil {
    /**
     * 父类构造方法，通过子类传入的配置类设置配置
     *
     * @param systemInfo
     */
    public FileUtils(SystemInfo systemInfo) {
        super(systemInfo);
        this.setPath("files");
    }
}
