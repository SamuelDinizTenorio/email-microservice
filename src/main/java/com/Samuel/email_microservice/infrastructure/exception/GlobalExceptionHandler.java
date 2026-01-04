package com.Samuel.email_service.infrastructure.exception;

import com.Samuel.email_service.core.exception.EmailServiceException;
import com.Samuel.email_service.infrastructure.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Manipula exceções específicas do serviço de e-mail.
     * Retorna um status 500 (Internal Server Error).
     */
    @ExceptionHandler(EmailServiceException.class)
    public ResponseEntity<ErrorDTO> handleEmailServiceException(EmailServiceException ex) {
        log.error("EmailServiceException: {}", ex.getMessage(), ex);
        ErrorDTO errorDto = new ErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
    }

    /**
     * Manipula exceções de validação de argumentos de método (acionadas pelo @Valid).
     * Retorna um status 400 (Bad Request) com detalhes sobre os campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("Validation error: {}", errors);

        ErrorDTO errorDto = new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed: " + errors.toString(),
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(errorDto);
    }

    /**
     * Manipula exceções quando o corpo da requisição JSON está malformado ou ilegível.
     * Retorna um status 400 (Bad Request).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("Malformed JSON in request body: {}", ex.getMessage());
        ErrorDTO errorDto = new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Malformed JSON in request body. Please check your request syntax.",
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(errorDto);
    }

    /**
     * Manipula todas as outras exceções não tratadas.
     * Retorna um status 500 (Internal Server Error) com uma mensagem genérica para segurança.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred", ex);

        ErrorDTO errorDto = new ErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please try again later.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
    }
}
