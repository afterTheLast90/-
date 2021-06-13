package com.hanhai.cloud.config;

import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import cn.dev33.satoken.router.SaRouterUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.constant.Pattern;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * @author wmgx
 * @create 2021-06-13-0:54
 **/
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    // 注册sa-token的登录拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 注册路由拦截器，自定义验证规则
        registry.addInterceptor(new SaRouteInterceptor((request, response, handler) -> {

            System.out.println(request.getRequestPath());
            // 登录验证 -- 排除多个路径
            SaRouterUtil.match(Arrays.asList("/**"), Arrays.asList(Pattern.publicPattern), () -> StpUtil.checkLogin());

            SaRouterUtil.match( Arrays.asList(Pattern.adminPattern),
                    () -> StpUtil.checkPermission("admin"));


        })).addPathPatterns("/**");
    }
}