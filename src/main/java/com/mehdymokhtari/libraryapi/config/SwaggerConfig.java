package com.mehdymokhtari.libraryapi.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

/**
 *
 *
 * <h1>API Interactive Documentation Configuration (SwaggerConfig)</h1>
 *
 * *
 *
 * <h3>1. WHAT IS THIS FILE DOING?</h3>
 *
 * This class instantiates and structures a customized {@link OpenAPI} specification bean. It
 * populates core developer meta-information, licensing policies, target server runtime hosts
 * (Local, Staging, Production), and application descriptors that power your interactive REST API
 * documentation portal. *
 *
 * <h3>2. WHY DO WE NEED IT?</h3>
 *
 * In industry-standard engineering, REST APIs require accessible documentation so that front-end
 * teams, QA, or external clients can see exactly what endpoints exist, what payloads they require,
 * and what statuses they return. Instead of writing tedious static documentation (like PDFs) that
 * quickly becomes outdated, this class enables **Swagger UI**. It automatically reads your Java
 * controllers and outputs a live, web-based sandbox dashboard where developers can try out requests
 * directly in their browser. *
 *
 * <h3>3. HOW DOES IT RUN? (Lifecycle & Inversion of Control)</h3>
 *
 * <ul>
 *   <li>Spring detects the {@code @Configuration} annotation on app initialization, creating the
 *       class instance.
 *   <li>It runs the {@code libraryOpenAPI()} method to build the custom {@link OpenAPI} data
 *       mapping layout.
 *   <li>The configured {@code OpenAPI} object is registered in the container as a managed bean.
 *   <li>The springdoc-openapi engine then intercepts this bean, parses your REST controllers, and
 *       renders the interactive UI at {@code http://localhost:8080/swagger-ui/index.html}. You
 *       never manually call this class from elsewhere in the codebase.
 * </ul>
 *
 * *
 *
 * <h3>4. ANNOTATIONS EXPLANATION:</h3>
 *
 * <ul>
 *   <li>{@code @Configuration}: Defines the class as a configuration source to establish
 *       infrastructure blueprints for the context.
 *   <li>{@code @Bean}: Registers the returned custom {@link OpenAPI} bean instance into the
 *       internal container engine so the swagger-ui engine can ingest it to generate user-friendly
 *       visual documentation.
 * </ul>
 *
 * *
 *
 * <h3>5. IMPORTANT ARCHITECTURAL NOTE:</h3>
 *
 * Your IDE might show "0 usages" or "No Usage" for this class. Do not delete it! Because Spring
 * scans packages and injects beans dynamically via reflection at runtime, static code parsers
 * cannot track these calls, causing the warning. Once your app is running, navigating to the
 * swagger-ui endpoint will display your customized title, your contact information, and a dropdown
 * menu allowing you to test endpoints against Local, Staging, or Production environments
 * seamlessly.
 */
@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI libraryOpenAPI() {
    return new OpenAPI()
        // Define metadata parameters (Title, Description, and Semantic Versioning)
        .info(
            new Info()
                .title("Library Management API")
                .description("Professional Library Management System REST API")
                .version("1.0.0")
                // Core developer and technical maintainer contact parameters
                .contact(
                    new Contact()
                        .name("Mehdy Mokhtari")
                        .email("mh.mokhtari7@gmail.com")
                        .url("https://github.com/MahdyMokh7"))
                // Application licensing strategy definitions
                .license(
                    new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
        // Configure isolated server target endpoints for live browser execution checks
        .servers(
            List.of(
                new Server().url("http://localhost:8080").description("Local Development Server"),
                new Server().url("http://staging.example.com").description("Staging Server"),
                new Server().url("https://api.library.com").description("Production Server")));
  }
}
