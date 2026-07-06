package com.mehdymokhtari.libraryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SuppressWarnings({"PMD.UseUtilityClass", "PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal"})
public class LibraryManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(LibraryManagementApplication.class, args);
  }
}
