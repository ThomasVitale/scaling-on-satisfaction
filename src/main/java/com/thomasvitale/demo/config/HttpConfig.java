package com.thomasvitale.demo.config;

import java.time.Duration;

import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;

@Configuration(proxyBeanMethods = false)
public class HttpConfig {

    @Bean
    RestClientCustomizer bufferingRestClientCustomizer() {
        JdkClientHttpRequestFactory jdkFactory = new JdkClientHttpRequestFactory();
        jdkFactory.setReadTimeout(Duration.ofSeconds(60));
        
        BufferingClientHttpRequestFactory bufferingFactory = new BufferingClientHttpRequestFactory(jdkFactory);
        
        return builder -> builder.requestFactory(bufferingFactory);
    }

}
