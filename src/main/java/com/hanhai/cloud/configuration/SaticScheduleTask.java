package com.hanhai.cloud.configuration;

import com.hanhai.cloud.service.RecycleService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author wmgx
 * @create 2021-06-14-0:01
 **/
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Log4j2
public class SaticScheduleTask {
    @Autowired
    RecycleService recycleService;
    @Scheduled(cron = "0 0 3 * * ?")
    private void configureTasks() {
        log.info("系统开始整理回收站逾期未删除的文件");
        recycleService.update30DaysNotDelete();
        log.info("回收站逾期未删除的文件整理完毕");
    }
}
