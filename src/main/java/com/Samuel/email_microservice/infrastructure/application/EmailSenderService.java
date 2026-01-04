package com.Samuel.email_microservice.infrastructure.application;

import com.Samuel.email_microservice.core.port.EmailSenderGateway;
import com.Samuel.email_microservice.core.usecase.EmailSenderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementação do {@link EmailSenderUseCase}.
 * Esta classe de serviço atua como o orquestrador do caso de uso de envio de e-mails.
 * Ela conecta a lógica de negócio (definida no core) com a infraestrutura (através do gateway).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderService implements EmailSenderUseCase {

    private final EmailSenderGateway emailSenderGateway;

    /**
     * Executa a ação de enviar um e-mail, delegando a tarefa para o gateway de e-mail.
     * Este método cumpre o contrato definido pela interface {@link EmailSenderUseCase}.
     *
     * @param to      O endereço de e-mail do destinatário.
     * @param subject O assunto do e-mail.
     * @param body    O corpo do e-mail.
     */
    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("Executing SendEmailUseCase for recipient {}", to);
        this.emailSenderGateway.sendEmail(to, subject, body);
    }
}
