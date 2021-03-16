package com.github.sparkzxl.mail.config;

import com.github.sparkzxl.mail.service.MailService;
import com.github.sparkzxl.mail.service.impl.MailServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import javax.annotation.Resource;

/**
 * description: mail邮件配置
 *
 * @author zhouxinlei
 */
@Configuration
@AutoConfigureAfter(MailSenderAutoConfiguration.class)
public class MainAutoConfiguration {

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private MailProperties mailProperties;

    @Bean
    @ConditionalOnBean({MailProperties.class, JavaMailSender.class})
    public MailService mailService() {

        return new MailServiceImpl(mailSender, mailProperties);
    }
}
