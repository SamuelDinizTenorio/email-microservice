package com.Samuel.email_service.application;

import com.Samuel.email_service.adapters.EmailSenderGateway;
import com.Samuel.email_service.core.EmailSenderUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementação do {@link EmailSenderUseCase}.
 * Esta classe de serviço atua como o orquestrador do caso de uso de envio de e-mails.
 * Ela conecta a lógica de negócio (definida no core) com a infraestrutura (através do gateway).
 */
@Slf4j
@Service
public class EmailSenderService implements EmailSenderUseCase {

    private final EmailSenderGateway emailSenderGateway;

    /**
     * Construtor que injeta a implementação do gateway de envio de e-mail.
     *
     * @param emailSenderGateway A implementação concreta do gateway (ex: AwsSesEmailSender) fornecida pelo Spring.
     */
    public EmailSenderService(EmailSenderGateway emailSenderGateway) {
        this.emailSenderGateway = emailSenderGateway;
    }

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
