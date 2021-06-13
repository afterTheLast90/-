package com.hanhai.cloud.controller;

import com.hanhai.cloud.base.R;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.systemInfo.SystemHardwareInfo;
import com.hanhai.cloud.systemInfo.SystemVO;
import com.hanhai.cloud.utils.BeanUtils;
import com.hanhai.cloud.utils.utils.SystemInfoRedisUtils;
import com.hanhai.cloud.vo.ChatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wmgx
 * @create 2021-05-17-15:54
 **/
@Controller
@Validated
public class DashBoardController {


    @Autowired
    SystemHardwareInfo systemHardwareInfo;
    @Autowired
    private SystemInfoRedisUtils systemInfoRedisUtils;


    @GetMapping("/dashBoard/visits")
    @ResponseBody
    public R<ChatVO> getNDayVisits(@RequestParam(value = "days",required = false,defaultValue = "7")  @Min(value = 7,
            message = "日期数量不合法") @Max(value = 30, message = "日期数量不合法") Integer days){


        List<String> x = new ArrayList<>(days);
        List<Object> v = new ArrayList<>(days);
        LocalDate now = LocalDate.now();
        for (int i = days-1; i >=0; i--) {
            LocalDate mins = now.minusDays(i);
            x.add(mins.format(DateTimeFormatter.ofPattern("MM-dd")));
            Integer count = (Integer) systemInfoRedisUtils.get( mins.toString());
            v.add(count==null?0:count);
        }
        return new R<ChatVO>(ResultCode.SUCCESS_NO_SHOW).setData(new ChatVO().setXAxisData(x).setSeriesData(v));
    }


    @GetMapping("/dashBoard/getSystemInfo")
    @ResponseBody
    public R getSystemInfo() throws InterruptedException {
        return R.getSuccess().setData(BeanUtils.convertTo(systemHardwareInfo.copyTo(), SystemVO.class));
    }



}
