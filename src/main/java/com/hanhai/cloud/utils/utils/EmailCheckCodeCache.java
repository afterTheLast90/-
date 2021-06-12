package com.hanhai.cloud.utils.utils;

import com.hanhai.cloud.utils.RedisBaseUtils;
import org.springframework.stereotype.Component;

/**
 * @author wmgx
 * @create 2021-02-01-14:31
 **/
@Component
public class EmailCheckCodeCache extends RedisBaseUtils {
    public EmailCheckCodeCache() {
        super("CHECK_CODE:EMAIL",5*60);
    }
}
