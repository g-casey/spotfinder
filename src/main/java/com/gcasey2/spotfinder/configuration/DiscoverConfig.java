package com.gcasey2.spotfinder.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;
import java.util.Random;

@Configuration
public class DiscoverConfig {

    @Autowired
    private Environment env;


    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .build();
    }

    @Bean
    public Random random(){
        return new Random();
    }

    @Bean
    public JavaMailSender getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(env.getProperty("spring.mail.host"));
        mailSender.setPort(Integer.valueOf(env.getProperty("spring.mail.port")));
        mailSender.setUsername(env.getProperty("spring.mail.username"));
        mailSender.setPassword(env.getProperty("spring.mail.password"));

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.starttls.enable", "true");
        javaMailProperties.put("mail.smtp.auth", "true");
        javaMailProperties.put("mail.transport.protocol", "smtp");

        mailSender.setJavaMailProperties(javaMailProperties);
        return mailSender;
    }

}
