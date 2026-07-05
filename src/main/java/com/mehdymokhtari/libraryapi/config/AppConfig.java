package com.mehdymokhtari.libraryapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Application-wide configuration class. Enables JPA auditing for automatic timestamp management and
 * configures CORS (Cross-Origin Resource Sharing) settings for the API.
 */
@Configuration
@EnableJpaAuditing
public class AppConfig {

  /**
   * Configures CORS (Cross-Origin Resource Sharing) settings for the API. Allows cross-origin
   * requests from any origin with standard HTTP methods, enabling frontend applications to consume
   * the API from different domains.
   *
   * @return WebMvcConfigurer with CORS configuration
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/api/**") // Apply to all API endpoints
            .allowedOrigins("*") // Allow requests from any origin
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
            .allowedHeaders("*") // Allow all headers
            .maxAge(3600); // Cache preflight response for 1 hour
      }
    };
  }
}
