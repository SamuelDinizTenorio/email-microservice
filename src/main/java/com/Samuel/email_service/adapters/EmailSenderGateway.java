package com.Samuel.email_service.adapters;

/**
 * Interface que define o contrato para o serviço de envio de e-mails.
 * Esta é uma abstração que permite a troca da implementação do provedor de e-mail
 * sem impactar a lógica de negócio da aplicação (o core).
 */
public interface EmailSenderGateway {

    /**
     * Envia um e-mail para o destinatário especificado.
     *
     * @param to      O endereço de e-mail do destinatário.
     * @param subject O assunto do e-mail.
     * @param body    O corpo do e-mail.
     * @throws com.Samuel.email_service.core.exception.EmailServiceException se ocorrer um erro durante o envio.
     */
    void sendEmail(String to, String subject, String body);
}
