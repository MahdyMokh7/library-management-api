package com.mehdymokhtari.libraryapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for Swagger/OpenAPI documentation.
 * Configures API metadata, contact information, and server environments
 * for the interactive API documentation available at /swagger-ui.html.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Creates and configures the OpenAPI bean with API documentation metadata.
     * Includes title, description, version, contact details, license information,
     * and available server environments (local, staging, production).
     *
     * @return Configured OpenAPI instance
     */
    @Bean
    public OpenAPI libraryOpenAPI() {
        return new OpenAPI()
                // API metadata configuration
                .info(new Info()
                        .title("Library Management API")
                        .description("Professional Library Management System REST API")
                        .version("1.0.0")
                        // Developer contact information
                        .contact(new Contact()
                                .name("Mehdy Mokhtari")
                                .email("mh.mokhtari7@gmail.com")
                                .url("https://github.com/MahdyMokh7"))
                        // License information
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                // Available server environments
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("http://staging.example.com")
                                .description("Staging Server"),
                        new Server()
                                .url("https://api.library.com")
                                .description("Production Server")
                ));
    }
}