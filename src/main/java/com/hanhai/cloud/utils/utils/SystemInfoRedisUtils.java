package com.hanhai.cloud.utils.utils;

import com.hanhai.cloud.utils.RedisBaseUtils;
import org.springframework.stereotype.Component;

/**
 * @author wmgx
 * @create 2021-05-17-10:41
 **/
@Component
public class SystemInfoRedisUtils extends RedisBaseUtils {
    public SystemInfoRedisUtils(){
        super("systemInfo",60*60*24*30);
    }
}
