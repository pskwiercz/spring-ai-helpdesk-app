package com.pskwiercz.app.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfiguration {
    @Bean
    public JavaMailSender createMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(EmailProperties.DEFAULT_HOST);
        mailSender.setPort(EmailProperties.DEFAULT_PORT);
        mailSender.setUsername(EmailProperties.DEFAULT_USERNAME);
        mailSender.setPassword(EmailProperties.DEFAULT_PASSWORD);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", EmailProperties.DEFAULT_AUTH);
        props.put("mail.smtp.starttls.enable", EmailProperties.DEFAULT_STARTTLS);
        return mailSender;
    }
}
