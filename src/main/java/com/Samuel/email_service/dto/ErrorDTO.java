package com.Samuel.email_service.dto;

import java.time.LocalDateTime;

/**
 * DTO para padronizar as respostas de erro da API.
 *
 * @param status    O código de status HTTP.
 * @param message   A mensagem de erro.
 * @param timestamp A data e hora em que o erro ocorreu.
 */
public record ErrorDTO(
        int status,
        String message,
        LocalDateTime timestamp
) {
}
