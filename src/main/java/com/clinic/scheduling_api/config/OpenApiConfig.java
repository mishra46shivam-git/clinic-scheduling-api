package com.clinic.scheduling_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI clinicSchedulingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clinic Scheduling REST API")
                        .description("Production-grade appointment scheduling engine featuring optimistic locking for race-condition prevention.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Clinic API Support")
                                .email("support@clinic.com")));
    }
}
