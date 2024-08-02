package com.bci.usersapp.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(servers = {@Server(url = "/")})
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        var contact = new Contact()
                .name("BCI")
                .email("bci@bci.cl");

        var licence = new License()
                .name("Apache 2.0")
                .url("http://springdoc.org");

        var info = new Info()
                .title("usersapp")
                .description("API to users")
                .version("1.0.0")
                .contact(contact)
                .license(licence);

        return new OpenAPI().info(info);
    }
}
