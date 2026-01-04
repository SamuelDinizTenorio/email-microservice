package com.Samuel.email_microservice.infrastructure.exception.helper;

import com.Samuel.email_microservice.core.exception.EmailServiceException;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@RestController
public class TestController {

    @GetMapping("/test/email-exception")
    void throwEmailServiceException() {
        throw new EmailServiceException("Email service failed");
    }

    @PostMapping("/test/validation")
    void validateInput(@RequestBody @Valid TestValidationDTO dto) {
        // Método vazio, apenas para validar o DTO
    }

    @GetMapping("/test/generic-exception")
    void throwGenericException() throws Exception {
        throw new Exception("Unexpected error");
    }

    @GetMapping("/test/force-404")
    void throwNoHandlerFoundException() throws NoHandlerFoundException {
        // Simula manualmente a exceção que o Spring lançaria se não encontrasse a rota
        throw new NoHandlerFoundException("GET", "/test/force-404", new HttpHeaders());
    }

    @GetMapping("/test/force-405")
    void throwMethodNotAllowedException() throws HttpRequestMethodNotSupportedException {
        throw new HttpRequestMethodNotSupportedException("POST", List.of("GET", "PUT"));
    }

    @GetMapping("/test/force-415")
    void throwUnsupportedMediaTypeException() throws HttpMediaTypeNotSupportedException {
        throw new HttpMediaTypeNotSupportedException(MediaType.APPLICATION_XML, List.of(MediaType.APPLICATION_JSON));
    }
}
