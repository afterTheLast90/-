package com.hanhai.cloud.service;

import com.hanhai.cloud.base.BaseService;
import org.springframework.stereotype.Service;

/**
 * @author wmgx
 * @create 2021-05-04-10:37
 **/
@Service
public class SystemSettingService extends BaseService {
    public String SelectById(String key){
        return systemSettingsMapper.selectById(key).getSettingValue();
    }
}
