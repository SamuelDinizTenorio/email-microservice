package com.Samuel.email_service.infrastructure.controller;

import com.Samuel.email_service.core.usecase.EmailSenderUseCase;
import com.Samuel.email_service.infrastructure.dto.EmailRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável por expor a funcionalidade de envio de e-mails via API REST.
 */
@Slf4j
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailSenderController {

    private final EmailSenderUseCase emailSenderUseCase;

    /**
     * Endpoint para enviar um e-mail.
     * Recebe os dados do e-mail no corpo da requisição e os valida.
     * Delega a lógica de envio para o caso de uso {@link EmailSenderUseCase}.
     *
     * @param request O DTO {@link EmailRequestDTO} contendo os dados do e-mail (destinatário, assunto, corpo).
     * @return Uma {@link ResponseEntity} com status 200 (OK) e uma mensagem de sucesso.
     *         Em caso de falha na validação, um erro 400 (Bad Request) será retornado automaticamente.
     *         Em caso de falha no envio, o GlobalExceptionHandler tratará a exceção e retornará um erro apropriado (ex: 500).
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody @Valid EmailRequestDTO request) {
        log.info("Received request to send email to {} with subject '{}'", request.to(), request.subject());
        this.emailSenderUseCase.sendEmail(request.to(), request.subject(), request.body());
        log.info("Email request for {} processed successfully", request.to());
        return ResponseEntity.ok("Email sent successfully");
    }
}
