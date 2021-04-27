package com.hanhai.cloud.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.github.yitter.idgen.YitIdHelper;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * @author wmgx
 * @create 2021-04-25-10:13
 **/
@Configuration
public class MybatisPlusConfig {

    /**
     * 自定义插入和更新的时候更新创建时间和更新时间
     * @return MetaObjectHandler
     */
    @Bean
    public MetaObjectHandler myMetaObjectHandler(){
        return new MetaObjectHandler(){
            @Override
            public void insertFill(MetaObject metaObject) {
                //  做在了数据库的部分直接指定了默认值
                // if (metaObject.hasSetter("createdTime")) metaObject.setValue("createdTime", LocalDateTime.now());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                if (metaObject.hasSetter("updatedTime")) metaObject.setValue("updatedTime", LocalDateTime.now());
            }
        };
    }


    /**
     * 自定义id生成器
     * 短的雪花id，避免前端long超范围
     * @return id生成器
     */
    @Bean
    public IdentifierGenerator myIdentifierGenerator(){

//        return new IdentifierGenerator(){
//            @Override
//            public Number nextId(Object entity) {
//                return YitIdHelper.nextId();
//            }
//
//        };
        return entity -> YitIdHelper.nextId();
    }
}
