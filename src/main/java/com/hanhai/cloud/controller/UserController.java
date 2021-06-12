package com.hanhai.cloud.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.hanhai.cloud.base.BaseException;
import com.hanhai.cloud.base.PageResult;
import com.hanhai.cloud.base.R;
import com.hanhai.cloud.configuration.SystemInfo;
import com.hanhai.cloud.constant.ResultCode;
import com.hanhai.cloud.entity.User;
import com.hanhai.cloud.exception.UpdateException;
import com.hanhai.cloud.params.RePasswordParams;
import com.hanhai.cloud.params.UserInfoParams;
import com.hanhai.cloud.params.UserInfoUpdateParams;
import com.hanhai.cloud.params.UserSearchParams;
import com.hanhai.cloud.service.UserService;
import com.hanhai.cloud.utils.*;
import com.hanhai.cloud.utils.utils.AvatarFileUtils;
import com.hanhai.cloud.utils.utils.SystemInfoRedisUtils;
import com.hanhai.cloud.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    AvatarFileUtils avatarFileUtils;

    @Autowired
    SystemInfoRedisUtils systemInfoRedisUtils;
    @Autowired
    private SystemInfo systemInfo;

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private SmsUtils smsUtils;

    @PutMapping("/userInfo/update")
    @ResponseBody
    public R updateUser(@RequestBody @Validated  UserInfoUpdateParams userInfoUpdateParams){
        System.out.println(userInfoUpdateParams);

        User userById = userService.getUserById(StpUtil.getLoginIdAsLong());

        BeanUtils.copyProperties(userInfoUpdateParams,userById);

        userService.updateById(userById);

        return R.getSuccess().setData(userById);
    }


    @Transactional
    @PostMapping("/userInfo/updateAvatar")
    @ResponseBody
    public R updateAvatar(MultipartFile file) throws IOException {
        String s = avatarFileUtils.saveFile(file);
        User userById = userService.getUserById(StpUtil.getLoginIdAsLong());
        userById.setUserAvatar(s);
        userService.updateById(userById);
        return R.getSuccess().setData(s);
    }


    @GetMapping("/avatar")
    public void getAvatar(@RequestParam(value = "avatar",required = true) String fileName, HttpServletResponse response) throws IOException {
//
//        InputStream file = avatarFileUtils.getFile(fileName);
//
//        response.getOutputStream().

        avatarFileUtils.getFileWrite(fileName,response);
    }

    @GetMapping("/avatar/{userId}")
    public void getAvatar(@PathVariable("userId") Long userId, HttpServletResponse response) throws IOException {

        User userById = userService.getUserById(userId);
        if (userById == null) {
            generateImg("无", response.getOutputStream());
            return;
        }
        if (StrUtil.isNotBlank(userById.getUserAvatar())) {
            try {
                avatarFileUtils.getFileWrite(userById.getUserAvatar(), false, response);
                response.getOutputStream().close();
                return;
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
        generateImg(userById.getUserName(), response.getOutputStream());

    }


    @GetMapping("/userManager")
    public String userManager(Model model) {

        model.addAttribute("defaultSpaceSize", systemInfo.getDefaultSpaceSize());
        return "userManager";
    }

    @GetMapping("/admin/users")
    @ResponseBody
    public R getAllUser(@Validated UserSearchParams param) {
        if (StrUtil.isBlank(param.getKey()))
            param.setKey("");
        return R.getSuccess().setData(new PageResult(userService.selectUser(param), UserInfoVO.class));
    }

    @PostMapping("/admin/user")
    @ResponseBody
    public R addUser(@RequestBody @Validated UserInfoParams userInfoParams) {
        if (!userService.checkUserPhone(userInfoParams.getUserPhone())) {
            throw new UpdateException().setMsg("手机号重复,请检查后再试");
        }
        if (!userService.checkUserEmail(userInfoParams.getUserEmail())) {
            throw new UpdateException().setMsg("Email重复,请检查后再试");
        }

        User user = BeanUtils.convertTo(userInfoParams, User.class);
        user.setUserPassword(PasswordEncryptionUtils.hashPassword(userInfoParams.getUserPassword()));
        userService.insertUser(user);
        return R.getSuccess().setMsg("添加成功");
    }

    @DeleteMapping("/admin/user/{userId}")
    @ResponseBody
    public R deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return R.getSuccess().setMsg("删除成功");
    }

    @PutMapping("/admin/user/{userId}")
    @ResponseBody
    public R updateUser(@PathVariable("userId") Long userId,
                        @RequestBody @Validated UserInfoParams userInfoParams) {

        User user = userService.getUserById(userId);

        if (!user.getUserPhone().equals(userInfoParams.getUserPhone()))
            if (!userService.checkUserPhone(userInfoParams.getUserPhone())) {
                throw new UpdateException().setMsg("手机号重复,请检查后再试");
            }

        if (!user.getUserEmail().equals(userInfoParams.getUserEmail()))
            if (!userService.checkUserEmail(userInfoParams.getUserEmail())) {
                throw new UpdateException().setMsg("Email重复,请检查后再试");
            }

        if (StrUtil.isNotBlank(userInfoParams.getUserPassword()))
            userInfoParams.setUserPassword(PasswordEncryptionUtils.hashPassword(userInfoParams.getUserPassword()));
        else
            userInfoParams.setUserPassword(user.getUserPassword());

        BeanUtils.copyProperties(userInfoParams, user);
        userService.updateById(user);
        return R.getSuccess().setMsg("修改成功");
    }

    @GetMapping("/admin/exportAll")
    public void exportAll(HttpServletResponse response) {
        List<User> all = userService.getAll();
        writeExcel(all, response);
    }

    @GetMapping("/admin/export")
    public void export(@Validated UserSearchParams param, HttpServletResponse response) {
        List<User> all = userService.selectUser(param);
        writeExcel(all, response);
    }

    @PostMapping("/admin/import")
    @Transactional
    @ResponseBody
    public R imp(@RequestParam("file") MultipartFile file) throws IOException {
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        List<Map<String, Object>> readAll = reader.readAll();
        List<Map<String, Object>> unSuccess = new ArrayList<>();
        int i = 0;
        for (Map<String, Object> map : readAll) {
            User user = new User();
            if (StrUtil.isBlank(String.valueOf(map.get("姓名")))) {
                map.put("错误原因", "姓名为空");
                unSuccess.add(map);
                continue;
            }
            user.setUserName(String.valueOf(map.get("姓名")));

            if (StrUtil.isBlank(String.valueOf(map.get("邮箱"))) || !Validator.isEmail(String.valueOf(map.get("邮箱")))) {
                map.put("错误原因", "邮箱为空或者非法");
                unSuccess.add(map);
                continue;
            }
            if (!userService.checkUserEmail(String.valueOf(map.get("邮箱")))) {
                map.put("错误原因", "邮箱已存在");
                unSuccess.add(map);
                continue;
            }
            user.setUserEmail(String.valueOf(map.get("邮箱")));
            if (StrUtil.isBlank(String.valueOf(map.get("手机号"))) || !Validator.isMobile(String.valueOf(map.get("手机号")))) {
                map.put("错误原因", "手机号为空或者非法");
                unSuccess.add(map);
                continue;
            }

            if (!userService.checkUserPhone(String.valueOf(map.get("手机号")))) {
                map.put("错误原因", "手机号已存在");
                unSuccess.add(map);
                continue;
            }
            user.setUserPhone(String.valueOf(map.get("手机号")));

            if (StrUtil.isBlank(String.valueOf(map.get("性别"))) || !"男女".contains(String.valueOf(map.get("性别")))) {
                map.put("错误原因", "性别为空或者非法");
                unSuccess.add(map);
                continue;
            }
            user.setUserGender("男".equals(map.get("性别")) ? 0 : 1);
            user.setUserPassword(PasswordEncryptionUtils.hashPassword("123456"));
            if (StrUtil.isBlank(String.valueOf(map.get("邮箱是否不需要验证"))) || !"是否".contains(String.valueOf(map.get("邮箱是否不需要验证")))) {
                map.put("错误原因", "邮箱是否不需要验证为空或者非法");
                unSuccess.add(map);
                continue;
            }

            user.setEmailChecked("是".equals(map.get("邮箱是否不需要验证")));
            if (StrUtil.isBlank(String.valueOf(map.get("手机号是否不需要验证"))) || !"是否".contains(String.valueOf(map.get("手机号是否不需要验证")))) {
                map.put("错误原因", "手机号是否不需要验证为空或者非法");
                unSuccess.add(map);
                continue;
            }

            user.setPhoneChecked("是".equals(map.get("手机号是否不需要验证")));
            if (StrUtil.isBlank(String.valueOf(map.get("是否是管理员"))) || !"是否".contains(String.valueOf(map.get("是否是管理员")))) {
                map.put("错误原因", "是否是管理员为空或者非法");
                unSuccess.add(map);
                continue;
            }
            user.setAdmin("是".equals(map.get("是否是管理员")));
            if (!Validator.isNumber(map.get("空间大小").toString())) {
                map.put("错误原因", "空间大小为空或者非法");
                unSuccess.add(map);
                continue;
            }
            user.setSpaceSize(Long.valueOf(map.get("空间大小").toString()));

            userService.insertUser(user);
            i++;
        }
        if (i != readAll.size()) {
            systemInfoRedisUtils.set("errorItems", unSuccess);
            return R.getSuccess().setMsg("成功导入" + i + "条，失败" + (readAll.size() - i) + "条");
        } else {
            systemInfoRedisUtils.delete("errorItems");
            return R.getSuccess().setMsg("全部导入成功");
        }
    }

    @GetMapping("/admin/template")
    public void template(HttpServletResponse response) {
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.addHeaderAlias("姓名", "姓名");
        writer.addHeaderAlias("性别", "性别");
        writer.addHeaderAlias("邮箱", "邮箱");
        writer.addHeaderAlias("手机号", "手机号");
        writer.addHeaderAlias("邮箱是否不需要验证", "邮箱是否不需要验证");
        writer.addHeaderAlias("手机号是否不需要验证", "手机号是否不需要验证");
        writer.addHeaderAlias("空间大小", "空间大小");
        writer.addHeaderAlias("是否是管理员", "是否是管理员");
        HashMap hashMap = new HashMap();
        hashMap.put("姓名", "demo");
        hashMap.put("性别", "男");
        hashMap.put("手机号", "15130991123");
        hashMap.put("邮箱", "demo@abc.com");
        hashMap.put("邮箱是否不需要验证", "是（是/否）");
        hashMap.put("手机号是否不需要验证", "是（是/否）");
        hashMap.put("空间大小", "1000(单位字节)");
        hashMap.put("是否是管理员", "是（是/否）");

        writer.write(Arrays.asList(hashMap), true);

        for (int i = 0; i < 8; i++) {
            writer.autoSizeColumn(i);
        }
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd " +
                "hh:mm:ss")) + "_template.xls");
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
        IoUtil.close(out);
    }

    @GetMapping("/admin/importError")
    public void importError(HttpServletResponse response){
        List<Map<String, Object>>  errorItems =    (List<Map<String, Object>> )systemInfoRedisUtils.get("errorItems");
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.addHeaderAlias("姓名", "姓名");
        writer.addHeaderAlias("性别", "性别");
        writer.addHeaderAlias("邮箱", "邮箱");
        writer.addHeaderAlias("手机号", "手机号");
        writer.addHeaderAlias("邮箱是否不需要验证", "邮箱是否不需要验证");
        writer.addHeaderAlias("手机号是否不需要验证", "手机号是否不需要验证");
        writer.addHeaderAlias("空间大小", "空间大小");
        writer.addHeaderAlias("是否是管理员", "是否是管理员");
        writer.addHeaderAlias("错误原因", "错误原因");

        writer.write(errorItems, true);

        for (int i = 0; i < 8; i++) {
            writer.autoSizeColumn(i);
        }
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd " +
                "hh:mm:ss")) + "_error.xls");
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
        IoUtil.close(out);
    }

    private void writeExcel(List<User> all, HttpServletResponse response) {
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.addHeaderAlias("userName", "姓名");
        writer.addHeaderAlias("userGender", "性别(0男1女)");
        writer.addHeaderAlias("userEmail", "邮箱");
        writer.addHeaderAlias("userPhone", "手机号");
        writer.addHeaderAlias("emailChecked", "邮箱是否不需要验证");
        writer.addHeaderAlias("phoneChecked", "手机号是否不需要验证");
        writer.addHeaderAlias("spaceSize", "空间大小（单位字节）");
        writer.addHeaderAlias("admin", "是否是管理员");
        List<Map> res = new ArrayList<>(all.size());
        for (User user : all) {
            Map<String, Object> objectMap = BeanUtil.beanToMap(user);
            objectMap.remove("userId");
            objectMap.remove("userPassword");
            objectMap.remove("userAvatar");
            objectMap.remove("usedSize");
            objectMap.remove("createdTime");
            objectMap.remove("updatedTime");
            objectMap.remove("deleted");
            res.add(objectMap);

        }
        writer.write(res, true);
        writer.autoSizeColumn(0);
        writer.autoSizeColumn(1);
        writer.autoSizeColumn(2);
        writer.autoSizeColumn(3);
        writer.autoSizeColumn(4);
        writer.autoSizeColumn(5);
        writer.autoSizeColumn(6);
        writer.autoSizeColumn(7);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");


        response.setHeader("Content-Disposition", "attachment;filename=" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd " +
                "hh:mm:ss")) + "_user.xls");
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
        IoUtil.close(out);
    }


    /**
     * 绘制字体头像 如果是英文名，只显示首字母大写 如果是中文名，只显示最后两个字
     *
     * @param name
     * @param outputStream
     *
     * @throws IOException
     */
    public void generateImg(String name, OutputStream outputStream)
            throws IOException {
        int width = 100;
        int height = 100;
        int nameLen = name.length();
        String nameWritten;
        //如果用户输入的姓名少于等于2个字符，不用截取
        if (nameLen <= 2) {
            nameWritten = name;
        } else {
            //如果用户输入的姓名大于等于3个字符，截取后面一位  可根据需求改长度
            String first = name.substring(0, 1);
            if (StringUtil.isChinese(first)) {
                //截取倒数两位汉字
                nameWritten = name.substring(nameLen - 1);
            } else {
                //截取前面的两个英文字母
                nameWritten = name.substring(0, 2).toUpperCase();
            }
        }
        //Font font = new Font("微软雅黑", Font.PLAIN, 30);

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setBackground(getRandomColor());

        g2.clearRect(0, 0, width, height);

        g2.setPaint(Color.WHITE);


        Font font = null;
        //两个字及以上
        if (nameWritten.length() >= 2) {
            font = new Font("微软雅黑", Font.PLAIN, 30);
            g2.setFont(font);

            String firstWritten = nameWritten.substring(0, 1);
            String secondWritten = nameWritten.substring(1, 2);

            //两个中文 如 言曌
            if (StringUtil.isChinese(firstWritten) && StringUtil.isChinese(secondWritten)) {
                g2.drawString(nameWritten, 20, 60);
            }
            //首中次英 如 罗Q
            else if (StringUtil.isChinese(firstWritten) && !StringUtil.isChinese(secondWritten)) {
                g2.drawString(nameWritten, 27, 60);

                //首英,如 AB
            } else {
                nameWritten = nameWritten.substring(0, 1);
            }

        }
        //一个字
        if (nameWritten.length() == 1) {
            //中文
            if (StringUtil.isChinese(nameWritten)) {
                font = new Font("微软雅黑", Font.PLAIN, 50);
                g2.setFont(font);
                g2.drawString(nameWritten, 25, 70);
            }
            //英文
            else {
                font = new Font("微软雅黑", Font.PLAIN, 55);
                g2.setFont(font);
                g2.drawString(nameWritten.toUpperCase(), 33, 67);
            }

        }

//        BufferedImage rounded = makeRoundedCorner(bi, 99);
        ImageIO.write(bi, "png", outputStream);
        outputStream.close();
    }


    /**
     * 获得随机颜色
     *
     * @return
     */
    private static Color getRandomColor() {
        String[] beautifulColors =
                new String[]{"232,221,203", "205,179,128", "3,101,100", "3,54,73", "3,22,52",
                        "237,222,139", "251,178,23", "96,143,159", "1,77,103", "254,67,101", "252,157,154",
                        "249,205,173", "200,200,169", "131,175,155", "229,187,129", "161,23,21", "34,8,7",
                        "118,77,57", "17,63,61", "60,79,57", "95,92,51", "179,214,110", "248,147,29",
                        "227,160,93", "178,190,126", "114,111,238", "56,13,49", "89,61,67", "250,218,141",
                        "3,38,58", "179,168,150", "222,125,44", "20,68,106", "130,57,53", "137,190,178",
                        "201,186,131", "222,211,140", "222,156,83", "23,44,60", "39,72,98", "153,80,84",
                        "217,104,49", "230,179,61", "174,221,129", "107,194,53", "6,128,67", "38,157,128",
                        "178,200,187", "69,137,148", "117,121,71", "114,83,52", "87,105,60", "82,75,46",
                        "171,92,37", "100,107,48", "98,65,24", "54,37,17", "137,157,192", "250,227,113",
                        "29,131,8", "220,87,18", "29,191,151", "35,235,185", "213,26,33", "160,191,124",
                        "101,147,74", "64,116,52", "255,150,128", "255,94,72", "38,188,213", "167,220,224",
                        "1,165,175", "179,214,110", "248,147,29", "230,155,3", "209,73,78", "62,188,202",
                        "224,160,158", "161,47,47", "0,90,171", "107,194,53", "174,221,129", "6,128,67",
                        "38,157,128", "201,138,131", "220,162,151", "137,157,192", "175,215,237", "92,167,186",
                        "255,66,93", "147,224,255", "247,68,97", "185,227,217"};
        int len = beautifulColors.length;
        Random random = new Random();
        String[] color = beautifulColors[random.nextInt(len)].split(",");
        return new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]),
                Integer.parseInt(color[2]));
    }

    @PostMapping("/forgetPassword")
    @ResponseBody
    public R forgotPassword(@Validated @RequestBody RePasswordParams rePasswordParams){
        if (Validator.isMobile(rePasswordParams.getAccount())) {
            if (!smsUtils.checkCode(rePasswordParams.getAccount(),rePasswordParams.getCaptcha())) {
                throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("验证码错误");
            }
            User user = userService.getUserByPhone(rePasswordParams.getAccount());
            if (user==null)
                throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("用户不存在");

            user.setUserPassword(PasswordEncryptionUtils.hashPassword(rePasswordParams.getNewPassword()));
            userService.updateById(user);
            return R.getSuccess().setMsg("密码重置成功");
        }
        if (Validator.isEmail(rePasswordParams.getAccount())){
            if (!mailUtils.checkCode(rePasswordParams.getAccount(),rePasswordParams.getCaptcha())) {
                throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("验证码错误");
            }
            User user = userService.getUserByEmail(rePasswordParams.getAccount());
            if (user==null)
                throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("用户不存在");

            user.setUserPassword(PasswordEncryptionUtils.hashPassword(rePasswordParams.getNewPassword()));
            userService.updateById(user);
            return R.getSuccess().setMsg("密码重置成功");

        }
        throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("请输入正确的手机号或邮箱");
    }
    @PostMapping("/send/captcha")
    @ResponseBody
    public R sendCaptcha(@RequestBody Map map) throws UnsupportedEncodingException, MessagingException {
        String account = (String) map.get("account");
        if (Validator.isMobile(account)){
            User user = userService.getUserByPhone(account);
            if (user==null)
                throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("用户不存在");

            if (!user.getPhoneChecked()){
                throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg(
                        "用户手机号未通过核验，请通过邮箱修改密码或者联系管理员");
            }

            smsUtils.send(account);
            return R.getSuccess().setMsg("发送成功");

        }
        if (Validator.isEmail(account)){
            User user = userService.getUserByEmail(account);
            if (user==null)
                throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("用户不存在");

            if (!user.getEmailChecked()){
                throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg(
                        "用户邮箱未通过核验，请通过手机号修改密码或者联系管理员");
            }

            mailUtils.sendCode(account);
            return R.getSuccess().setMsg("发送成功");

        }
        throw new BaseException(ResultCode.PARAMETER_ERROR).setMsg("请输入正确的手机号或邮箱");
    }

}
