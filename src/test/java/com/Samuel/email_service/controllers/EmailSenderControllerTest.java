package com.Samuel.email_service.controllers;

import com.Samuel.email_service.core.EmailSenderUseCase;
import com.Samuel.email_service.core.exception.EmailServiceException;
import com.Samuel.email_service.dto.EmailRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(EmailSenderController.class)
@DisplayName("Email Sender Controller Tests")
class EmailSenderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailSenderUseCase emailSenderUseCase;

    @Test
    @DisplayName("Should return HTTP 200 OK when the email request is valid")
    void sendEmail_withValidRequest_shouldReturnOk() throws Exception {
        // Arrange
        EmailRequestDTO request = new EmailRequestDTO("test@example.com", "Test Subject", "Test Body");

        // Act & Assert
        mockMvc.perform(post("/api/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent successfully"));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when the email format is invalid")
    void sendEmail_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        // Arrange
        EmailRequestDTO request = new EmailRequestDTO("invalid-email", "Test Subject", "Test Body");

        // Act & Assert
        mockMvc.perform(post("/api/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Invalid email format")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when the subject is blank")
    void sendEmail_withBlankSubject_shouldReturnBadRequest() throws Exception {
        // Arrange
        EmailRequestDTO request = new EmailRequestDTO("test@example.com", "", "Test Body");

        // Act & Assert
        mockMvc.perform(post("/api/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Subject cannot be blank")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when the body is blank")
    void sendEmail_withBlankBody_shouldReturnBadRequest() throws Exception {
        // Arrange
        EmailRequestDTO request = new EmailRequestDTO("test@example.com", "Test Subject", "");

        // Act & Assert
        mockMvc.perform(post("/api/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Body cannot be blank")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when the JSON is empty")
    void sendEmail_withEmptyJson_shouldReturnBadRequest() throws Exception {
        // Arrange - No specific request DTO needed, just an empty JSON

        // Act & Assert
        mockMvc.perform(post("/api/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Recipient email cannot be blank")))
                .andExpect(jsonPath("$.message", containsString("Subject cannot be blank")))
                .andExpect(jsonPath("$.message", containsString("Body cannot be blank")));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when the JSON is malformed")
    void sendEmail_withMalformedJson_shouldReturnBadRequest() throws Exception {
        // Arrange - Malformed JSON content

        // Act & Assert
        mockMvc.perform(post("/api/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"to\": \"test@example.com\", \"subject\":}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Malformed JSON in request body")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return HTTP 500 Internal Server Error when the email service fails")
    void sendEmail_whenServiceFails_shouldReturnInternalServerError() throws Exception {
        // Arrange
        EmailRequestDTO request = new EmailRequestDTO("test@example.com", "Service Failure Test", "Test Body");

        // Configura o mock para lançar uma exceção quando o método sendEmail for chamado
        doThrow(new EmailServiceException("Failed to connect to email provider"))
                .when(emailSenderUseCase).sendEmail(request.to(), request.subject(), request.body());

        // Act & Assert
        mockMvc.perform(post("/api/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Failed to connect to email provider"))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}