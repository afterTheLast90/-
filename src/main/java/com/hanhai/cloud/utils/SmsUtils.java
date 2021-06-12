package com.hanhai.cloud.utils;

import cn.hutool.core.util.RandomUtil;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.hanhai.cloud.configuration.SystemInfo;
import com.hanhai.cloud.exception.SmsException;
import com.hanhai.cloud.params.AlySmsTestParams;
import com.hanhai.cloud.utils.utils.PhoneCheckCodeCache;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wmgx
 * @create 2021-01-31-17:05
 **/
@Component
@Log4j2
public class SmsUtils {
    @Autowired
    PhoneCheckCodeCache phoneCheckCodeCache;
    DefaultProfile profile = null;
    IAcsClient client = null;


    private SystemInfo systemInfo;

    public SmsUtils(@Autowired SystemInfo systemInfo) {
        this.systemInfo =systemInfo;
        update();

    }

    public void update(){
        profile= DefaultProfile.getProfile(systemInfo.getAlySmsRegionId(), systemInfo.getAlySmsAccessKeyId(),
                systemInfo.getAlySmsSecret());
        client = new DefaultAcsClient(profile);
    }

    public void send(String phoneNumber){
        if (phoneCheckCodeCache.get(phoneNumber + "ONEMINE") != null) {
            throw new SmsException("发送验证码过于频繁，一分钟只能发送一次");
        }
        Integer count = (Integer) phoneCheckCodeCache.get(phoneNumber + "FIVEMINE");
        if (count != null && count == 2) {
            throw new SmsException( "发送验证码过于频繁，五分钟只能发送两次");
        } else {
            if (count == null) count = 0;
        }

        String code = RandomUtil.randomNumbers(6);
        send(client,phoneNumber,systemInfo.getAlySmsTemplateCode(),code);
        count++;
        phoneCheckCodeCache.set(phoneNumber, code);
        phoneCheckCodeCache.set(phoneNumber + "ONEMINE", 1, 60);
        phoneCheckCodeCache.set(phoneNumber + "FIVEMINE", count, 5 *60);

    }
    private void send(IAcsClient client,String phoneNumber,String templateCode, String code) throws SmsException {

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysAction("SendSms");
        request.setSysVersion("2017-05-25");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", "励志不负青春");
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info(response.getData());
        } catch (ClientException e) {
            e.printStackTrace();
            throw new SmsException( "发送验证码失败，请稍后再试或联系管理员");
        }

    }

    public boolean checkCode(String phoneNumber, String c) {
        Object code = phoneCheckCodeCache.get(phoneNumber);
        if (code != null && code.toString().equals(c)) {
            removeCode(phoneNumber);
            return true;
        } else {
            return false;
        }
    }

    public void removeCode(String phoneNumber){
        phoneCheckCodeCache.delete(phoneNumber + "ONEMINE");
        phoneCheckCodeCache.delete(phoneNumber + "FIVEMINE");
        phoneCheckCodeCache.delete(phoneNumber);
    }

    public Boolean check(String phoneNumber, String c){
        Object code = phoneCheckCodeCache.get(phoneNumber);
        return code != null && code.toString().equals(c);
    }
    public void sendTest(AlySmsTestParams alySmsTestParams){
        DefaultProfile profile= DefaultProfile.getProfile(alySmsTestParams.getAlySmsRegionId(),
                alySmsTestParams.getAlySmsAccessKeyId(),
                alySmsTestParams.getAlySmsSecret());

        IAcsClient  client = new DefaultAcsClient(profile);
        send(client,alySmsTestParams.getTestPhoneRec(),alySmsTestParams.getAlyTemplateCode(),
                "TestMessage");

    }

}
