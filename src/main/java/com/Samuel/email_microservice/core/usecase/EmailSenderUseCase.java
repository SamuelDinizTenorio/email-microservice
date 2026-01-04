package com.Samuel.email_microservice.core.usecase;

/**
 * Define o caso de uso para o envio de e-mails.
 * Esta interface representa a principal funcionalidade de negócio da aplicação,
 * orquestrando o processo de envio de um e-mail.
 */
public interface EmailSenderUseCase {

    /**
     * Executa o caso de uso de envio de e-mail.
     *
     * @param to      O endereço de e-mail do destinatário.
     * @param subject O assunto do e-mail.
     * @param body    O corpo do e-mail.
     */
    void sendEmail(String to, String subject, String body);
}
