package com.hanhai.cloud;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import top.wmgx.annotation.MybatisLogPlusAutoConfig;

@EnableTransactionManagement
@MybatisLogPlusAutoConfig
@MapperScan("com.hanhai.cloud.mapper")
@SpringBootApplication
public class CloudApplication {

    private static ConfigurableApplicationContext context;
    public static void main(String[] args) {
        idGeneratorConfig();
        context=SpringApplication.run(CloudApplication.class, args);
    }

    public static void restartApplication() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        Thread thread = new Thread(() -> {
            context.close();
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            context = SpringApplication.run(CloudApplication.class, args.getSourceArgs());
        });

        thread.setDaemon(false);
        thread.start();
    }
    /**
     * 配置id生成
     * <a href ="https://gitee.com/yitter/idgenerator/tree/master/Java">文档</a>
     */
    public static void idGeneratorConfig() {
        // 创建 IdGeneratorOptions 对象，请在构造函数中输入 WorkerId：
        IdGeneratorOptions options = new IdGeneratorOptions((short) 1);
        // options.WorkerIdBitLength = 10; // WorkerIdBitLength 默认值6，支持的 WorkerId 最大值为2^6-1，若 WorkerId 超过64，可设置更大的 WorkerIdBitLength
        // ...... 其它参数设置参考 IdGeneratorOptions 定义，一般来说，只要再设置 WorkerIdBitLength （决定 WorkerId 的最大值）。

        // 保存参数（必须的操作，否则以上设置都不能生效）：
        YitIdHelper.setIdGenerator(options);
        // 以上初始化过程只需全局一次，且必须在第2步之前设置。


        // 初始化以后，即可在任何需要生成ID的地方，调用以下方法：
        //long newId = YitIdHelper.nextId();
    }
}
