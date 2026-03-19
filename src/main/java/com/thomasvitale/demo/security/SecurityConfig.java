package com.thomasvitale.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf
                        // Safe to disable for JSON API endpoints (same-origin JS, no form submissions)
                        .ignoringRequestMatchers("/api/**", "/admin/api/**"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/story", "/login").permitAll()
                        .requestMatchers("/api/story/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .defaultSuccessUrl("/admin"))
                .build();
    }

}
