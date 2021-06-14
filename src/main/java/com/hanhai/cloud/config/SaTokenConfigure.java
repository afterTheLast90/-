package com.hanhai.cloud.config;

import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import cn.dev33.satoken.router.SaRouterUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.hanhai.cloud.configuration.SystemInfo;
import com.hanhai.cloud.constant.Pattern;
import com.hanhai.cloud.utils.utils.SystemInfoRedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * @author wmgx
 * @create 2021-06-13-0:54
 **/
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    @Autowired
    SystemInfo systemInfo;
    @Autowired
    SystemInfoRedisUtils systemInfoRedisUtils;
    // 注册sa-token的登录拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 注册路由拦截器，自定义验证规则
        registry.addInterceptor(new SaRouteInterceptor((request, response, handler) -> {


            if (systemInfo.getInstalled()) {
                System.out.println(request.getRequestPath());
                systemInfoRedisUtils.incr(LocalDate.now().toString());
                // 登录验证 -- 排除多个路径
                SaRouterUtil.match(Arrays.asList("/**"), Arrays.asList(Pattern.publicPattern),
                        () -> StpUtil.checkLogin());

                SaRouterUtil.match(Arrays.asList(Pattern.adminPattern),
                        () -> StpUtil.checkPermission("admin"));
                // 系统安装完成之后不能访问
                SaRouterUtil.match(Arrays.asList(Pattern.installPattern),
                        () -> StpUtil.checkPermission("cannotAccess"));
            }else{
                SaRouterUtil.match(Arrays.asList("/**"), Arrays.asList(Pattern.installPatternWithPublic),
                        () -> StpUtil.checkLogin());

            }

        })).addPathPatterns("/**");
    }
}