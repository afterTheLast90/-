package com.hanhai.cloud.utils;

import cn.hutool.core.util.RandomUtil;
import com.hanhai.cloud.configuration.SystemInfo;
import com.hanhai.cloud.exception.SmsException;
import com.hanhai.cloud.params.SmtpTestParams;
import com.hanhai.cloud.utils.utils.EmailCheckCodeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

/**
 * 本类用于邮件发送
 *
 * @author 李昊天
 */
@Component
public class MailUtils {
    private   Properties props = new Properties(); // 参数配置

    private   Session session;

    private SystemInfo systemInfo;
    @Autowired
    private EmailCheckCodeCache emailCheckCodeCache;
    public void  updateInfo(){
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", systemInfo.getSmtpServer());
        props.setProperty("mail.smtp.port",systemInfo.getSmtpPort().toString());
        props.setProperty("mail.smtp.auth", "true");
        session = Session.getInstance(props
//                ,new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                String userName = systemInfo.getSmtpUsername();
//                String password = systemInfo.getSmtpPassword();
//                return new PasswordAuthentication(userName, password);
//            }
//        }
        );
        session.setDebug(true);
    }

    public MailUtils(@Autowired SystemInfo systemInfo){
        this.systemInfo=systemInfo;
        updateInfo();
    }
    /**
     * 本方法用于邮件发送
     *
     * @param toEmail
     *         要发送给的Email
     * @param title
     *         邮件标题
     * @param content
     *         邮件内容
     *
     */
    public void send(String toEmail, String title, String content) throws MessagingException, UnsupportedEncodingException {

        Transport transport = session.getTransport();
        transport.connect(systemInfo.getSmtpUsername(), systemInfo.getSmtpPassword());
        MimeMessage message = new MimeMessage(session);
        message.setSubject(title);
        message.setFrom(new InternetAddress(systemInfo.getSmtpUsername(), systemInfo.getSmtpSender(), "UTF-8"));
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(toEmail, "hello", "UTF-8"));
        BodyPart m = new MimeBodyPart();
        m.setContent(content, "text/html;charset=UTF-8");
        // 创建多重消息
        Multipart multipart = new MimeMultipart();

        // 设置文本消息部分
        multipart.addBodyPart(m);

        message.setContent(multipart);
        message.setSentDate(new Date());
        message.saveChanges();

        transport.sendMessage(message,message.getAllRecipients());

        transport.close();

    }



    public  void send(SmtpTestParams smtpTestParams) throws MessagingException, UnsupportedEncodingException {


        Properties props = new Properties(); // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", smtpTestParams.getSmtpServer());
        props.setProperty("mail.smtp.port",smtpTestParams.getSmtpPort().toString());
        props.setProperty("mail.smtp.auth", "true");
        Session session= Session.getInstance(props);

        session.setDebug(true);
        Transport transport = session.getTransport();

        transport.connect(smtpTestParams.getSmtpUsername(), smtpTestParams.getSmtpPassword());
        MimeMessage message = new MimeMessage(session);
        message.setSubject("测试邮件");
        message.setFrom(new InternetAddress(smtpTestParams.getSmtpUsername(), smtpTestParams.getSmtpSender(),
                "UTF-8"));
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(smtpTestParams.getTestRec(),
                "hello", "UTF-8"));
        BodyPart m = new MimeBodyPart();
        m.setContent("这是一封测试邮件，当你接收到这封邮件代表smtp服务器配置成功。", "text/html;charset=UTF-8");
        // 创建多重消息
        Multipart multipart = new MimeMultipart();

        // 设置文本消息部分
        multipart.addBodyPart(m);

        message.setContent(multipart);
        message.setSentDate(new Date());
        message.saveChanges();

        transport.sendMessage(message,message.getAllRecipients());

        transport.close();

    }

    public void sendCode(String toEmail) throws UnsupportedEncodingException, MessagingException {
        if (emailCheckCodeCache.get(toEmail + "ONEMINE") != null) {
            throw new SmsException("发送验证码过于频繁，一分钟只能发送一次");
        }
        Integer count = (Integer) emailCheckCodeCache.get(toEmail + "FIVEMINE");
        if (count != null && count == 2) {
            throw new SmsException( "发送验证码过于频繁，五分钟只能发送两次");
        } else {
            if (count == null) count = 0;
        }

        String code = RandomUtil.randomNumbers(6);
        send(toEmail,"验证码","验证码："+"<br>" +
                        "<b>"+
                        code+
                        "</b><br>" + "五分钟内有效，请尽快使用");
        count++;
        emailCheckCodeCache.set(toEmail, code);
        emailCheckCodeCache.set(toEmail + "ONEMINE", 1, 60);
        emailCheckCodeCache.set(toEmail + "FIVEMINE", count, 5 *60);
    }
    public Boolean checkCode(String toEmail,String c){
        Object code = emailCheckCodeCache.get(toEmail);
        if (code != null && code.toString().equals(c)) {
            removeCode( toEmail);
            return true;
        } else {
            return false;
        }
    }


    public void removeCode(String toEmail){
        emailCheckCodeCache.delete(toEmail + "ONEMINE");
        emailCheckCodeCache.delete(toEmail + "FIVEMINE");
        emailCheckCodeCache.delete(toEmail);
    }

    public Boolean check(String toEmail, String c){
        Object code = emailCheckCodeCache.get(toEmail);
        return code != null && code.toString().equals(c);
    }
 }
