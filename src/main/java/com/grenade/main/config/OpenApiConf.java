package com.grenade.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@OpenAPIDefinition(
    info = @Info(title = "LineUp API", version = "0.7"),
    tags = {
        @Tag(name = "auth"),
        @Tag(name = "user"),
        @Tag(name = "admin"),
        @Tag(name = "public")
    }
)

@Configuration
public class OpenApiConf {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement()
            .addList("token"))
        .components(new Components()
                .addSecuritySchemes("token",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .bearerFormat("JWT")
                        .scheme("bearer")));
    }
}
