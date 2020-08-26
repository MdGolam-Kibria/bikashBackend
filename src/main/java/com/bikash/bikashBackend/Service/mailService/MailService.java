package com.bikash.bikashBackend.Service.mailService;

import com.bikash.bikashBackend.Service.mailService.forSession.EmailConstrint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

@Configuration("mailservice")
public class MailService {
    private static final Logger logger = LogManager.getLogger(MailService.class.getName());
    private final JavaMailSender mailSender;
    private final EmailConstrint emailConstrint;

    @Autowired
    public MailService(JavaMailSender mailSender, EmailConstrint emailConstrint) {
        this.mailSender = mailSender;
        this.emailConstrint = emailConstrint;
    }

    private EmailStatus sendMimeMail(String[] to, String subject, String body, Boolean isHtml, List<File> attachment) {
        try {//mail send hote o pare na o pare sei Exception er jonno try catch
            //for mail send we need to make a session this session include javax.mail
            Session session = emailConstrint.getSessionInstance();//this is my custom making session object
//        Since we send MIME mail so..
            MimeMessage message = new MimeMessage(session);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);//true dewer reason hosse file thakuk ar na thakuk kono problem jeno na hoi
            helper.setFrom(emailConstrint.username);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, isHtml);
            if (attachment != null && attachment.size() > 0) {//jodi file thake
                attachment.forEach(file -> {
                    try {
                        helper.addAttachment(file.getName(), file);
                    } catch (MessagingException e) {
                        logger.error("Error Attachment " + e.getMessage());
                    }
                });
            }
            //for send mail we need a help  from this below class
            Transport.send(message);
            return new EmailStatus(to, subject, body).success();
        } catch (MessagingException e) {
            logger.error("Error is Sending Mail to " + to[0] + "Message  " + e.getMessage());
            return new EmailStatus(to, subject, body).error();
        }
    }

    public EmailStatus sendHtmlMail(String[] to, String subject, String body) {

        return sendMimeMail(to, subject, body, true, null);
    }

    public EmailStatus sendHtmlMail(String[] to, String subject, String body, List<File> attachment) {

        return sendMimeMail(to, subject, body, true, attachment);
    }

    public EmailStatus sendNonHtmlMail(String[] to, String subject, String body) {

        return sendMimeMail(to, subject, body, false, null);
    }

    public EmailStatus sendNonHtmlMail(String[] to, String subject, String body, List<File> attachment) {

        return sendMimeMail(to, subject, body, false, attachment);
    }

}
