package com.thomasvitale.demo.config;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.security.config.observation.SecurityObservationSettings;

@Configuration(proxyBeanMethods = false)
public class ObservabilityConfig {

    // See https://docs.spring.io/spring-security/reference/servlet/integrations/observability.html
    @Bean
    SecurityObservationSettings noSpringSecurityObservations() {
        return SecurityObservationSettings.noObservations();
    }

    // See https://github.com/spring-projects/spring-boot/issues/34801
    @Bean
    ObservationPredicate noActuatorServerObservations() {
        return (name, context) -> {
            if (name.equals("http.server.requests") && context instanceof ServerRequestObservationContext serverContext) {
                return !serverContext.getCarrier().getRequestURI().startsWith("/actuator");
            } else {
                return true;
            }
        };
    }

}
