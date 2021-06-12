package com.hanhai.cloud.utils.utils;

import com.hanhai.cloud.utils.RedisBaseUtils;
import org.springframework.stereotype.Component;

/**
 * @author wmgx
 * @create 2021-02-01-14:31
 **/
@Component
public class PhoneCheckCodeCache extends RedisBaseUtils {
    public PhoneCheckCodeCache() {
        super("CHECK_CODE:PHONE",5*60);
    }
}
