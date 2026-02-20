package com.qoqtest.notes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("The Notes App API")
                        .version("1.0")
                        .description("Test Assignment: A Spring Boot & MongoDB application for storing everyday notes with text analytics.")
                        .contact(new Contact()
                                .name("Developer")));
    }
}
