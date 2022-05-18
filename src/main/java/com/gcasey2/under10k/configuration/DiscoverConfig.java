package com.gcasey2.under10k.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Random;

@Configuration
public class DiscoverConfig {

    @Autowired
    private ApplicationContext context;

    @Bean
    public WebClient getWebClient(){
        return WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .build();
    }

    @Bean
    public Random getRandom(){
        return new Random();
    }

}
