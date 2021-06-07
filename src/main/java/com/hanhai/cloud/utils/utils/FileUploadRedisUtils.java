package com.hanhai.cloud.utils.utils;

import com.hanhai.cloud.utils.RedisBaseUtils;
import org.springframework.stereotype.Component;

/**
 * @author 郝天乐
 */
@Component
public class FileUploadRedisUtils extends RedisBaseUtils {
    public FileUploadRedisUtils(){
        super("uploadInfo",60*60*24*30);
    }
}
