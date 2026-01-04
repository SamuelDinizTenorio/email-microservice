package com.Samuel.email_microservice.infrastructure.controller;

import com.Samuel.email_microservice.core.usecase.EmailSenderUseCase;
import com.Samuel.email_microservice.core.exception.EmailServiceException;
import com.Samuel.email_microservice.infrastructure.dto.EmailRequestDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        mockMvc.perform(post("/api/email/send")
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
        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/email/send"))
                .andExpect(jsonPath("$.errors.to").value("Invalid email format"))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when the subject is blank")
    void sendEmail_withBlankSubject_shouldReturnBadRequest() throws Exception {
        // Arrange
        EmailRequestDTO request = new EmailRequestDTO("test@example.com", "", "Test Body");

        // Act & Assert
        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/email/send"))
                .andExpect(jsonPath("$.errors.subject").value("Subject cannot be blank"))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when the body is blank")
    void sendEmail_withBlankBody_shouldReturnBadRequest() throws Exception {
        // Arrange
        EmailRequestDTO request = new EmailRequestDTO("test@example.com", "Test Subject", "");

        // Act & Assert
        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/email/send"))
                .andExpect(jsonPath("$.errors.body").value("Body cannot be blank"))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when the JSON is empty")
    void sendEmail_withEmptyJson_shouldReturnBadRequest() throws Exception {
        // Arrange - No specific request DTO needed, just an empty JSON

        // Act & Assert
        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/email/send"))
                .andExpect(jsonPath("$.errors.to").value("Recipient email cannot be blank"))
                .andExpect(jsonPath("$.errors.subject").value("Subject cannot be blank"))
                .andExpect(jsonPath("$.errors.body").value("Body cannot be blank"))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when the JSON is malformed")
    void sendEmail_withMalformedJson_shouldReturnBadRequest() throws Exception {
        // Arrange - Malformed JSON content

        // Act & Assert
        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"to\": \"test@example.com\", \"subject\":}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString("Malformed JSON in request body")))
                .andExpect(jsonPath("$.path").value("/api/email/send"))
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
        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Failed to connect to email provider"))
                .andExpect(jsonPath("$.path").value("/api/email/send"))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return HTTP 405 Method Not Allowed when using GET instead of POST")
    void sendEmail_withWrongHttpMethod_shouldReturnMethodNotAllowed() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/email/send"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").value("Method Not Allowed"))
                .andExpect(jsonPath("$.message", containsString("Method Not Allowed")))
                .andExpect(jsonPath("$.path").value("/api/email/send"))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return HTTP 415 Unsupported Media Type when Content-Type is not JSON")
    void sendEmail_withWrongContentType_shouldReturnUnsupportedMediaType() throws Exception {
        // Arrange
        String xmlContent = "<request><to>test@example.com</to></request>";

        // Act & Assert
        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(xmlContent))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.error").value("Unsupported Media Type"))
                .andExpect(jsonPath("$.message", containsString("Unsupported Media Type")))
                .andExpect(jsonPath("$.path").value("/api/email/send"))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
