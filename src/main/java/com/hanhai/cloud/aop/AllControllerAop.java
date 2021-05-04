package com.hanhai.cloud.aop;

import cn.hutool.json.JSONUtil;
import com.hanhai.cloud.base.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wmgx
 * @create 2021-04-27-20:16
 **/
@Slf4j
@Component
@Aspect
public class AllControllerAop {
    /**
     * ..表示包及子包 该方法代表controller层的所有方法  TODO 路径需要根据自己项目定义
     */
    @Pointcut("execution(public * com.hanhai.cloud.controller.*.* (..))")
    public void controllerMethod() {
    }


    /**
     * 方法执行前
     *
     * @param joinPoint
     * @throws Exception
     */
    @Before("controllerMethod()")
    public void LogRequestInfo(JoinPoint joinPoint) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        StringBuilder requestLog = new StringBuilder();
        Signature signature = joinPoint.getSignature();
//        // 获取swager的注解
//        ApiOperation annotation = ((MethodSignature) signature)
//                .getMethod()
//                .getAnnotation(ApiOperation.class);
//        if (annotation!=null){
//            requestLog.append(annotation.value()).append("\t");
//        }


        requestLog.append("请求信息：").append("URL = {").append(request.getRequestURI()).append("},\t")
                .append("请 求方式 = {").append(request.getMethod()).append("},\t")
                .append("请求IP = {").append(request.getRemoteAddr()).append("},\t")
                .append("类方法 = {").append(signature.getDeclaringTypeName()).append(".")
                .append(signature.getName()).append("},\t");

        // 处理请求参数
        String[] paramNames = ((MethodSignature) signature).getParameterNames();
        Object[] paramValues = joinPoint.getArgs();
        int paramLength = null == paramNames ? 0 : paramNames.length;
        if (paramLength == 0) {
            requestLog.append("请求参数 = {} ");
        } else {
            requestLog.append("请求参数 = [");
            for (int i = 0; i < paramLength - 1; i++) {
                requestLog.append(paramNames[i]).append("=").append(JSONUtil.toJsonStr(paramValues[i])).append(",");
            }
            requestLog.append(paramNames[paramLength - 1]).append("=").append(JSONUtil.toJsonStr(paramValues[paramLength - 1])).append("]");
        }

        log.info(requestLog.toString());
    }



    @AfterReturning(returning = "r", pointcut =
            "controllerMethod()")
    public void logResultVOInfo(R r) {
        log.info("请求结果：" + r.getCode() + "\t" + r.getMsg()+"\t"+ JSONUtil.toJsonStr(r.getData()));
    }

    @AfterReturning(returning = "s", pointcut =
            "controllerMethod()")
    public void logResultVOInfo(String s) {
        log.info("页码跳转到"+s);
//        log.info("请求结果：" + r.getCode() + "\t" + r.getMsg()+"\t"+ JSONUtil.toJsonStr(r.getData()));
    }
}
