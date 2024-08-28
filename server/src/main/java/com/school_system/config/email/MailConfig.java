package com.school_system.config.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String auth;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String tslEnable;
    @Value("${spring.mail.properties.mail.smtp.starttls.required}")
    private String tslRequired;
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setProtocol("smtp");
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.getJavaMailProperties().setProperty("mail.smtp.auth", auth);
        mailSender.getJavaMailProperties().setProperty("mail.smtp.starttls.enable", tslEnable);
        mailSender.getJavaMailProperties().setProperty("mail.smtp.starttls.required", tslRequired);

        return mailSender;
    }
}
