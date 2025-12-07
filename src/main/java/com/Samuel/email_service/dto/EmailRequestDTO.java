package com.Samuel.email_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) que representa a requisição para envio de um e-mail.
 * Este record é usado para deserializar o corpo JSON da requisição recebida pelo controller.
 *
 * @param to      O endereço de e-mail do destinatário. Não pode ser nulo/vazio e deve ser um e-mail válido.
 * @param subject O assunto do e-mail. Não pode ser nulo/vazio.
 * @param body    O corpo do e-mail. Não pode ser nulo/vazio.
 */
public record EmailRequestDTO(
        @NotBlank(message = "Recipient email cannot be blank")
        @Email(message = "Invalid email format")
        String to,

        @NotBlank(message = "Subject cannot be blank")
        String subject,

        @NotBlank(message = "Body cannot be blank")
        String body
) {
}
