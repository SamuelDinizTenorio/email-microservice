package com.Samuel.email_microservice.infrastructure.exception;

import com.Samuel.email_microservice.infrastructure.exception.helper.TestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Global Exception Handler Tests (MockMvc)")
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Configura o MockMvc em modo standalone com um Controller de teste
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should handle EmailServiceException and return 500")
    void handleEmailServiceException_shouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/test/email-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.message", is("Email service failed")))
                .andExpect(jsonPath("$.path", is("/test/email-exception")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException and return 400")
    void handleValidationExceptions_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Corpo vazio para disparar erro de validação @NotNull
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.path", is("/test/validation")))
                .andExpect(jsonPath("$.errors.field", is("must not be null")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException (Malformed JSON) and return 400")
    void handleHttpMessageNotReadableException_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Malformed JSON in request body. Please check your request syntax.")))
                .andExpect(jsonPath("$.path", is("/test/validation")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should handle NoHandlerFoundException (404) and return 404")
    void handleNoHandlerFoundException_shouldReturnNotFound() throws Exception {
        // Chamamos um endpoint específico que lança a exceção manualmente para testar o Handler
        mockMvc.perform(get("/test/force-404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("O endpoint '")))
                .andExpect(jsonPath("$.message", containsString("/test/force-404' não foi encontrado.")))
                .andExpect(jsonPath("$.path", is("/test/force-404")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should handle HttpRequestMethodNotSupportedException (405) and return 405")
    void handleHttpRequestMethodNotSupportedException_shouldReturnMethodNotAllowed() throws Exception {
        mockMvc.perform(get("/test/force-405"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status", is(405)))
                .andExpect(jsonPath("$.error", is("Method Not Allowed")))
                .andExpect(jsonPath("$.message", containsString("Method Not Allowed")))
                .andExpect(jsonPath("$.path", is("/test/force-405")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should handle HttpMediaTypeNotSupportedException (415) and return 415")
    void handleHttpMediaTypeNotSupportedException_shouldReturnUnsupportedMediaType() throws Exception {
        mockMvc.perform(get("/test/force-415"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status", is(415)))
                .andExpect(jsonPath("$.error", is("Unsupported Media Type")))
                .andExpect(jsonPath("$.message", containsString("Unsupported Media Type")))
                .andExpect(jsonPath("$.path", is("/test/force-415")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should handle Generic Exception and return 500")
    void handleGenericException_shouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/test/generic-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.message", is("An unexpected error occurred. Please try again later.")))
                .andExpect(jsonPath("$.path", is("/test/generic-exception")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
