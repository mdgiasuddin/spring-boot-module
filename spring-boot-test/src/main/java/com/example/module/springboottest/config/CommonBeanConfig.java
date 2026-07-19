package com.example.module.springboottest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.UrlHandlerFilter;

@Configuration
public class CommonBeanConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:8190")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Configures an UrlHandlerFilter to handle trailing slashes in URLs.
     *
     * @return The configured UrlHandlerFilter.
     */
    @Bean
    public UrlHandlerFilter trailingSlashFilter() {
        return UrlHandlerFilter.trailingSlashHandler("/**")
                .wrapRequest()
                .build();
    }
}
