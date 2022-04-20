package com.gcasey2.under10k.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DiscoverConfig {
    @Bean
    public WebClient getWebClient(){
        return WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .build();
    }
}
