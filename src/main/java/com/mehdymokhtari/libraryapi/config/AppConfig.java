package com.mehdymokhtari.libraryapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 *
 * <h1>Web & HTTP Layer Configuration (AppConfig)</h1>
 *
 * *
 *
 * <h3>1. WHAT IS THIS FILE DOING?</h3>
 *
 * This class configures the web application layer properties. Specifically, it overrides Spring
 * MVC's default security behavior to establish a custom Cross-Origin Resource Sharing (CORS)
 * policy. It explicitly grants frontend applications operating on different origins (e.g., React on
 * localhost:3000, Angular, or Vue) permissions to securely interact with this backend REST API. *
 *
 * <h3>2. WHY DO WE NEED IT?</h3>
 *
 * By default, modern web browsers enforce the Same-Origin Policy (SOP), which prevents a web
 * application loaded from one origin from communicating with APIs hosted on a completely different
 * origin. Without this configuration, front-end apps would throw strict "CORS Blocked" errors when
 * attempting to call your HTTP endpoints. Furthermore, by stripping out data-layer (JPA)
 * requirements from this class, it is safe to be swept by lightweight Web slice-tests (like
 * {@code @WebMvcTest}), preventing test context initialization failures. *
 *
 * <h3>3. HOW DOES IT RUN? (Lifecycle & Inversion of Control)</h3>
 *
 * <ul>
 *   <li>During startup, Spring Boot boots up the main method and initiates a <b>Classpath Component
 *       Scan</b>.
 *   <li>It discovers this class inside the configuration package due to the {@code @Configuration}
 *       stereotype.
 *   <li>Spring instantly instantiates this class and registers it into the <b>Spring Application
 *       Context</b>.
 *   <li>It evaluates the methods inside; when it hits {@code corsConfigurer()}, it processes the
 *       inner code, produces the {@link WebMvcConfigurer} object, and holds it inside its Bean
 *       registry container.
 *   <li>When an HTTP request strikes the server, Spring MVC queries this container for CORS beans
 *       and applies the rules.
 * </ul>
 *
 * *
 *
 * <h3>4. ANNOTATIONS EXPLANATION:</h3>
 *
 * <ul>
 *   <li>{@code @Configuration}: Indicates that this class is a source of bean definitions. It tells
 *       the Spring IoC container that the class contains methods annotated with {@code @Bean} that
 *       must be executed to generate objects.
 *   <li>{@code @Bean}: Placed explicitly on a method to declare that the returned object should be
 *       fully managed by the Spring container as a singleton component available for dependency
 *       injection.
 * </ul>
 *
 * *
 *
 * <h3>5. IMPORTANT ARCHITECTURAL NOTE:</h3>
 *
 * The settings explicitly allow all origins ({@code allowedOrigins("*")}), custom headers, and
 * standard REST methods (GET, POST, PUT, DELETE, OPTIONS). The HTTP OPTIONS request is a
 * "Preflight" check sent by browsers to confirm safety before firing the actual payload; {@code
 * maxAge(3600)} instructs the browser to cache this clearance for 1 hour to significantly reduce
 * duplicate network traffic overhead.
 */
@Configuration
public class AppConfig {

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/api/**") // Apply CORS rules exclusively to routes starting with /api/
            .allowedOrigins("*") // Accept inbound client requests from any domain origin
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Authorized HTTP verbs
            .allowedHeaders("*") // Support all incoming request header configurations
            .maxAge(3600); // Preflight cache lifespan in seconds (1 Hour)
      }
    };
  }
}
