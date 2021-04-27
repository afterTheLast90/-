package com.hanhai.cloud.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author wmgx
 * @create 2021-04-27-20:32
 **/
@Configuration
@Data
public class FileUtilConfiguration {

    @Value("${system.file.basePath}")
    private  String basePath="";
}
