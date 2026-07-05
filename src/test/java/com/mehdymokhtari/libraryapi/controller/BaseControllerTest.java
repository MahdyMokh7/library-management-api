package com.mehdymokhtari.libraryapi.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mehdymokhtari.libraryapi.service.BookService;
import com.mehdymokhtari.libraryapi.service.BorrowingService;

@WebMvcTest
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class) // Forces Spring to mock the missing DB Metamodel
public abstract class BaseControllerTest {

  @Autowired protected MockMvc mockMvc;

  @Autowired protected ObjectMapper objectMapper;

  @MockBean protected BookService bookService;

  @MockBean protected BorrowingService borrowingService;

  protected String asJsonString(final Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to serialize object to JSON string", e);
    }
  }

  @TestConfiguration
  static class AuditTestConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
      return () -> Optional.of("TEST_USER");
    }
  }
}
