package com.Samuel.email_service.infrastructure.ses;

import com.Samuel.email_service.core.port.EmailSenderGateway;
import com.Samuel.email_service.core.exception.EmailServiceException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implementação do {@link EmailSenderGateway} que utiliza o Amazon Simple Email Service (SES).
 * Esta classe é responsável por toda a lógica de comunicação com a AWS para o envio de e-mails.
 */
@Slf4j
@Service
public class AwsSesEmailSender implements EmailSenderGateway {

    private final AmazonSimpleEmailService amazonSimpleEmailService;
    private final String mail;

    /**
     * Construtor para injeção de dependências.
     *
     * @param amazonSimpleEmailService O cliente do AWS SES, configurado e fornecido pelo Spring.
     * @param mail O endereço de e-mail do remetente, injetado a partir da propriedade "spring.mail.username".
     */
    public AwsSesEmailSender(AmazonSimpleEmailService amazonSimpleEmailService,
                             @Value("${spring.mail.username}") String mail) {
        this.amazonSimpleEmailService = amazonSimpleEmailService;
        this.mail = mail;
    }

    /**
     * Envia um e-mail utilizando o AWS SES.
     * Constrói a requisição de envio e a submete ao serviço da AWS.
     * Em caso de falha na comunicação com a AWS, captura a exceção específica do serviço
     * e a encapsula em uma {@link EmailServiceException} para abstrair a falha do resto da aplicação.
     *
     * @param to      O endereço de e-mail do destinatário.
     * @param subject O assunto do e-mail.
     * @param body    O corpo do e-mail em formato de texto.
     * @throws EmailServiceException se ocorrer um erro durante a comunicação com o AWS SES.
     */
    @Override
    public void sendEmail(String to, String subject, String body) {
        SendEmailRequest request = new SendEmailRequest()
                .withSource(mail)
                .withDestination(new Destination().withToAddresses(to))
                .withMessage(new Message()
                        .withSubject(new Content().withCharset("UTF-8").withData(subject))
                        .withBody(new Body().withText(new Content().withCharset("UTF-8").withData(body)))
                );

        try {
            log.info("Attempting to send email to {} via AWS SES", to);
            this.amazonSimpleEmailService.sendEmail(request);
            log.info("Email sent successfully to {}", to);
        } catch (AmazonServiceException ex) {
            log.error("Failed to send email to {} via AWS SES. AWS Error: {}", to, ex.getErrorMessage(), ex);
            // Encapsula a exceção da AWS em uma exceção de domínio para não vazar detalhes de infraestrutura.
            throw new EmailServiceException("Failure while sending email", ex);
        }
    }
}
