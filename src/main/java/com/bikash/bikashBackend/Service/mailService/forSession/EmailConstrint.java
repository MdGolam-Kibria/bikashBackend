package com.bikash.bikashBackend.Service.mailService.forSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Configuration
public class EmailConstrint {


    ;
    //this userName must match with the application.properties spring.mail.username
    @Value("${spring.mail.username}")
    public String username;
    @Value("${spring.mail.password}")
    public String password;
    @Value("${spring.mail.port}")
    public String port;
    @Value("${spring.mail.host}")
    public String host;
    @Value("${spring.mail.isAuth}")
    public String isAuth;//for check user authenticate or not
    @Value("${spring.mail.tls}")
    public String isTls;

    private Properties getPropertiesInstance() {
        Properties properties = new Properties();

        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.auth", isAuth);
        properties.put("mail.smtp.starttls.enable", isTls);
        return properties;
    }

    public Session getSessionInstance() {
        return Session.getInstance(getPropertiesInstance(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username,password);
            }
        });
    }
}
