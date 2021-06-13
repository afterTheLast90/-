package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.hanhai.cloud.base.BaseException;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.configuration.SystemInfo;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.User;
import com.hanhai.cloud.params.LoginParams;
import com.hanhai.cloud.params.RegisterParams;
import com.hanhai.cloud.service.UserService;
import com.hanhai.cloud.utils.BeanUtils;
import com.hanhai.cloud.utils.MailUtils;
import com.hanhai.cloud.utils.PasswordEncryptionUtils;
import com.hanhai.cloud.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wmgx
 * @create 2021-04-29-22:44
 **/
@Controller
@Slf4j
public class LoginController {
    @Autowired
    UserService userService;

    @Autowired
    SystemInfo systemInfo;

    @Autowired
    MailUtils mailUtils;

    @Autowired
    SmsUtils smsUtils;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("systemInfo", systemInfo);
        return "login";
    }

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {

        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 40, 4, 4);
        request.getSession().setAttribute("captcha", lineCaptcha.getCode());
        response.setContentType("image/png");
        lineCaptcha.write(response.getOutputStream());
        response.getOutputStream().close();
    }

    @PostMapping("/login")
    @ResponseBody
    public R loginSystem(HttpServletRequest request,
                         @RequestBody @Validated LoginParams loginParams) {
//        log.info("请求登录"+loginParams.toString());
        if (!loginParams.getCaptcha().equals(request.getSession().getAttribute("captcha")))
            return new R(ResultCode.CAPTCHA_ERROR);
        request.getSession().setAttribute("captcha", null);
        User user = userService.getUserByEmailAndPhone(loginParams.getUsername());
        if (user == null)
            return new R(ResultCode.LOGIN_ERROR);


        if (PasswordEncryptionUtils.checkPassword(loginParams.getPassword(), user.getUserPassword())) {
//            StpUtil.setLoginId(user.getUserId(),loginParams.getRememberMe());
            // 当开放注册之后，必须要验证手机号和邮箱才能登录
            if (!systemInfo.getOpenRegistration()) {
                StpUtil.setLoginId(user.getUserId());
                return new R(ResultCode.SUCCESS).setMsg("登录成功");
            } else {
                if (user.getPhoneChecked() && user.getEmailChecked()) {
                    StpUtil.setLoginId(user.getUserId());
                    return new R(ResultCode.SUCCESS).setMsg("登录成功");
                } else {
                    // 密码通过但是手机号和邮箱没有核验，管理员添加的用户
                    Map<String, Object> res = new HashMap();
                    res.put("phoneChecked", user.getPhoneChecked());
                    res.put("emailChecked", user.getEmailChecked());
                    res.put("phone", DesensitizedUtil.mobilePhone(user.getUserPhone()));
                    res.put("email", DesensitizedUtil.email(user.getUserEmail()));
                    request.getSession().setAttribute("userId", user.getUserId());
                    return new R(ResultCode.NOT_VERIFIED).setMsg("邮箱或者手机号未完成核验请先核验邮箱和手机号").setData(res);
                }
            }
        }

        return new R(ResultCode.LOGIN_ERROR);

    }

    /**
     * 登录前验证邮箱发送邮件
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     */
    @PostMapping("/verified/send/email")
    @ResponseBody
    public R verifiedSendMail(HttpServletRequest request) throws UnsupportedEncodingException,
            MessagingException {
        if (request.getSession().getAttribute("userId") == null) {
            return new R(ResultCode.PARAMETER_ERROR).setMsg("请先登录");
        }

        Long userId = (Long) request.getSession().getAttribute("userId");
        User user = userService.getUserById(userId);

        if (!user.getEmailChecked()) {
            mailUtils.sendCode(user.getUserEmail());
            return R.getSuccess().setMsg("邮件已经发送并及时查收，并验证邮箱");
        }
        return new R(ResultCode.PARAMETER_ERROR).setMsg("邮箱核验已经通过，请刷新页面再试");
    }

    /**
     * 登录前验证手机号发送短信
     * @param request
     * @return
     */
    @PostMapping("/verified/send/sms")
    @ResponseBody
    public R verifiedSendSms(HttpServletRequest request) {
        if (request.getSession().getAttribute("userId") == null) {
            return new R(ResultCode.PARAMETER_ERROR).setMsg("请先登录");
        }

        Long userId = (Long) request.getSession().getAttribute("userId");
        User user = userService.getUserById(userId);

        if (!user.getPhoneChecked()) {
            smsUtils.send(user.getUserPhone());
            return R.getSuccess().setMsg("短信已经发送并及时查收，并验证手机号");
        }
        return new R(ResultCode.PARAMETER_ERROR).setMsg("手机号核验已经通过，请刷新页面再试");
    }

    @PostMapping("/verified")
    @ResponseBody
    public R verified(@RequestBody Map map, HttpServletRequest request) {

        if (request.getSession().getAttribute("userId") == null) {
            return new R(ResultCode.PARAMETER_ERROR).setMsg("请先登录");
        }
        String emailCaptchaCode = map.get("emailCaptchaCode") == null ? "" :
                map.get("emailCaptchaCode").toString();

        String phoneCaptchaCode = map.get("phoneCaptchaCode") == null ? "" :
                map.get("phoneCaptchaCode").toString();

        Long userId = (Long) request.getSession().getAttribute("userId");
        User user = userService.getUserById(userId);

        String msg = "";
        if (!user.getEmailChecked()) {
            if (mailUtils.checkCode(user.getUserEmail(), emailCaptchaCode)) {
                user.setEmailChecked(true);
                userService.updateById(user);
                msg = "邮箱验证通过";
            } else {
                msg = "邮箱验证码错误";
            }
        }

        if (!user.getPhoneChecked()) {
            if (smsUtils.checkCode(user.getUserPhone(), phoneCaptchaCode)) {
                user.setPhoneChecked(true);
                userService.updateById(user);
                msg += ",手机验证通过";
            } else
                msg += ",手机验证码错误";

        }

        if (msg.startsWith(","))
            msg = msg.substring(1);


        Map<String, Object> res = new HashMap();
        res.put("phoneChecked", user.getPhoneChecked());
        res.put("emailChecked", user.getEmailChecked());
        return R.getSuccess().setMsg(msg).setData(res);

    }


    @PostMapping("/register/send/sms")
    @ResponseBody
    public R registerSendSms(@RequestBody Map map){
        Object account = map.get("account");

        if (!Validator.isMobile(account.toString())){
            return new R(ResultCode.PARAMETER_ERROR).setMsg("手机号格式错误");
        }

        if (!userService.checkUserPhone(account.toString())){
            return new R(ResultCode.PARAMETER_ERROR).setMsg("手机号已存在，请检查后再试");
        }
        smsUtils.send(account.toString());

        return R.getSuccess().setMsg("信息已发送，请及时验证");

    }



    @PostMapping("/register/send/email")
    @ResponseBody
    public R registerSendEmail(@RequestBody Map map) throws UnsupportedEncodingException, MessagingException {
        Object account = map.get("account");

        if (!Validator.isEmail(account.toString())){
            return new R(ResultCode.PARAMETER_ERROR).setMsg("邮箱格式错误");
        }

        if (!userService.checkUserEmail(account.toString())){
            return new R(ResultCode.PARAMETER_ERROR).setMsg("邮箱已存在，请检查后再试");
        }
        mailUtils.sendCode(account.toString());

        return R.getSuccess().setMsg("信息已发送，请及时验证");

    }


    @PostMapping("/register")
    @ResponseBody
    public R register(@Validated @RequestBody RegisterParams registerParams){
        if (!smsUtils.check(registerParams.getUserPhone(),
                registerParams.getRegisterPhoneVerificationCode())){
            return new R(ResultCode.PARAMETER_ERROR).setMsg("手机验证码错误");
        }
        if (!mailUtils.check(registerParams.getUserEmail(),registerParams.getRegisterEmailVerificationCode())){
            return new R(ResultCode.PARAMETER_ERROR).setMsg("邮箱验证码错误");
        }

        User user = BeanUtils.convertTo(registerParams, User.class);
        user.setPhoneChecked(true);
        user.setEmailChecked(true);
        user.setUserPassword(PasswordEncryptionUtils.hashPassword(registerParams.getUserPassword()));
        user.setSpaceSize(systemInfo.getDefaultSpaceSize());
        user.setUsedSize(0L);
        userService.insertUser(user);
        return R.getSuccess().setMsg("注册成功，欢迎使用"+systemInfo.getSiteName()+"系统");
    }

    @PostMapping("/verified/email/send")
    @ResponseBody
    public R verifiedEmailSend(@RequestBody Map map) throws UnsupportedEncodingException, MessagingException {
        Object objMail = map.get("email");
        Object objIsModify = map.get("modify");

        if (objMail==null || !Validator.isEmail(objMail.toString())){
            throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("邮箱格式错误");
        }
        if (objIsModify==null || (!"true".equals(objIsModify) && "false".equals(objIsModify)) ){
            throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("参数错误");
        }


        boolean isModify = Boolean.parseBoolean(objIsModify.toString());

        if (isModify){
            if (!userService.checkUserEmail(objMail.toString())){
                throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("邮箱已存在");
            }
        }
        mailUtils.sendCode(objMail.toString());

        return R.getSuccess().setMsg("邮箱验证码发送成功");
    }

    @PostMapping("/verified/sms/send")
    @ResponseBody
    public R verifiedSmsSend(@RequestBody Map map)  {
        Object objPhone = map.get("phone");
        Object objIsModify = map.get("modify");

        if (objPhone==null || !Validator.isMobile(objPhone.toString())){
            throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("手机号格式错误");
        }
        if (objIsModify==null || (!"true".equals(objIsModify) && "false".equals(objIsModify)) ){
            throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("参数错误");
        }


        boolean isModify = Boolean.parseBoolean(objIsModify.toString());

        if (isModify){
            if (!userService.checkUserPhone(objPhone.toString())){
                throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("手机号已存在");
            }
        }
        smsUtils.send(objPhone.toString());

        return R.getSuccess().setMsg("手机验证码发送成功");
    }

    @PostMapping("/verified/sms")
    @ResponseBody
    public R verifiedSms(@RequestBody Map map)  {
        Object objPhone = map.get("phone");
        Object objCode = map.get("code");
        if (objPhone==null || !Validator.isMobile(objPhone.toString())){
            throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("手机号格式错误");
        }
        if (objCode==null || StrUtil.isBlank(objCode.toString())){
            throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("参数错误");
        }

        if (smsUtils.checkCode(objPhone.toString(),objCode.toString())){
            User user = userService.getUserById(StpUtil.getLoginIdAsLong());
            user.setUserPhone(objPhone.toString());
            user.setPhoneChecked(true);
            userService.updateById(user);
            return R.getSuccess().setMsg("验证通过");
        }

        throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("验证码错误");
    }
    @PostMapping("/verified/Email")
    @ResponseBody
    public R verifiedEmail(@RequestBody Map map)  {
        Object objEmail = map.get("email");
        Object objCode = map.get("code");
        if (objEmail==null || !Validator.isEmail(objEmail.toString())){
            throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("邮箱格式错误");
        }
        if (objCode==null || StrUtil.isBlank(objCode.toString())){
            throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("参数错误");
        }

        if (mailUtils.checkCode(objEmail.toString(),objCode.toString())){
            User user = userService.getUserById(StpUtil.getLoginIdAsLong());
            user.setUserEmail(objEmail.toString());
            user.setEmailChecked(true);
            userService.updateById(user);
            return R.getSuccess().setMsg("验证通过");
        }

        throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("验证码错误");
    }

    @GetMapping("/logout")
    public String logout(){
        StpUtil.logout();
        return "redirect:login";
    }

}
