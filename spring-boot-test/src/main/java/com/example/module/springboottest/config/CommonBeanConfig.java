package com.example.module.springboottest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.UrlHandlerFilter;

@Configuration
public class CommonBeanConfig {

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
