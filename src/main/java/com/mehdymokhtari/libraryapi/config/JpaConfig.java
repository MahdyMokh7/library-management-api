package com.mehdymokhtari.libraryapi.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 *
 *
 * <h1>Data Tier & Relational Database Configuration (JpaConfig)</h1>
 *
 * *
 *
 * <h3>1. WHAT IS THIS FILE DOING?</h3>
 *
 * This dedicated configuration class isolates all database, entity management, repository wiring,
 * and auditing lifecycle parameters for the persistent storage layer. It provides targeted commands
 * telling Spring where to find database models and repository interfaces, while spinning up
 * automated lifecycle tracking. *
 *
 * <h3>2. WHY DO WE NEED IT?</h3>
 *
 * <ul>
 *   <li><b>Clean Architecture & Separation of Concerns:</b> Previously, these annotations were
 *       positioned on the main application startup class. This severely broke controller unit
 *       testing because slice-tests (like {@code @WebMvcTest}) purposely do not spin up the actual
 *       database environment. Placing database triggers on the bootstrapper forced tests to seek
 *       database connections, throwing a fatal {@code JPA metamodel must not be empty} error.
 *   <li><b>Automated Auditing:</b> Rather than manually writing boilerplate code to record record
 *       creation or update timestamps, this file sets up the foundation for Spring Data to automate
 *       it seamlessly via JPA entity lifecycle events.
 * </ul>
 *
 * *
 *
 * <h3>3. HOW DOES IT RUN? (Lifecycle & Inversion of Control)</h3>
 *
 * <ul>
 *   <li>Upon system bootstrapping, Spring scans the packages and identifies the
 *       {@code @Configuration} marker.
 *   <li>Spring instantly processes the metadata instructions stamped on top of the class
 *       definition.
 *   <li>It activates Hibernate/JPA repositories, loads database mappings, and binds automated
 *       listeners into the runtime engine.
 *   <li>Crucially, during unit/slice testing of web controllers, Spring Boot intentionally bypasses
 *       this file because it does not hold any web-layer beans, keeping the test environment fast,
 *       decoupled, and error-free.
 * </ul>
 *
 * *
 *
 * <h3>4. ANNOTATIONS EXPLANATION:</h3>
 *
 * <ul>
 *   <li>{@code @Configuration}: Designates this class as a functional block of infrastructure setup
 *       that provisions internal components for the Spring container context.
 *   <li>{@code @EnableJpaAuditing}: Powers up transparent auditing. It tracks annotations inside
 *       entity classes like {@code @CreatedDate} and {@code @LastModifiedDate}, automatically
 *       injecting database timestamps whenever records are written or updated.
 *   <li>{@code @EntityScan}: Instructs the Spring container where to look for Java classes
 *       annotated with {@code @Entity}, mapping those files directly to your underlying SQL
 *       database tables.
 *   <li>{@code @EnableJpaRepositories}: Informs the infrastructure to scan the designated package
 *       for interfaces extending {@code JpaRepository}, dynamically generating operational SQL
 *       proxy implementations behind the scenes so database interaction functions automatically.
 * </ul>
 */
@Configuration
@EnableJpaAuditing
@EntityScan(basePackages = "com.mehdymokhtari.libraryapi.model.entity")
@EnableJpaRepositories(basePackages = "com.mehdymokhtari.libraryapi.repository")
public class JpaConfig {
  // Intentionally left empty. Serves purely as a centralized bootstrap anchor for data-tier
  // annotations.
}
