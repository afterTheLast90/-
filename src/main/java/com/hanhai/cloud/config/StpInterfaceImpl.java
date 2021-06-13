package com.hanhai.cloud.config;

import cn.dev33.satoken.stp.StpInterface;
import com.hanhai.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wmgx
 * @create 2021-06-13-1:12
 **/
@Component    // 保证此类被SpringBoot扫描，完成sa-token的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    UserService userService;
    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginKey) {
        // 本list仅做模拟，实际项目中要根据具体业务逻辑来查询权限
        List<String> list = new ArrayList<String>();
        if (userService.getUserById(Long.parseLong(loginId.toString())).getAdmin())
            list.add("admin");
        return list;
    }

    @Override
    public List<String> getRoleList(Object o, String s) {
        return null;
    }


}