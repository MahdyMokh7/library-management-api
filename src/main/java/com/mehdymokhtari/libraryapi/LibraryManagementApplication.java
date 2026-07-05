package com.mehdymokhtari.libraryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(
    basePackages =
        "com.mehdymokhtari.libraryapi.model.entity") // manually finding instead of auto scanning
@EnableJpaRepositories(
    basePackages =
        "com.mehdymokhtari.libraryapi.repository") // manually finding instead of auto scanning
public class LibraryManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(LibraryManagementApplication.class, args);
  }
}
