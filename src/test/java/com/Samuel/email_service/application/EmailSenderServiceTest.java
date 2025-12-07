package com.Samuel.email_service.application;

import com.Samuel.email_service.adapters.EmailSenderGateway;
import com.Samuel.email_service.core.exception.EmailServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Email Sender Service Tests")
class EmailSenderServiceTest {

    @Mock
    private EmailSenderGateway emailSenderGateway;

    @InjectMocks
    private EmailSenderService emailSenderService;

    @Test
    @DisplayName("Should call EmailSenderGateway.sendEmail with correct arguments")
    void sendEmail_shouldCallGatewaySendEmail() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        emailSenderService.sendEmail(to, subject, body);

        // Verifica se o método sendEmail do gateway foi chamado exatamente uma vez com os argumentos corretos
        verify(emailSenderGateway, times(1)).sendEmail(to, subject, body);
    }

    @Test
    @DisplayName("Should throw EmailServiceException when EmailSenderGateway throws EmailServiceException")
    void sendEmail_shouldThrowExceptionWhenGatewayThrowsException() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";
        EmailServiceException gatewayException = new EmailServiceException("Gateway failed to send email");

        // Configura o mock para lançar uma exceção quando o método sendEmail for chamado
        doThrow(gatewayException).when(emailSenderGateway).sendEmail(to, subject, body);

        // Verifica se o serviço lança a mesma exceção
        EmailServiceException thrown = assertThrows(EmailServiceException.class, () -> {
            emailSenderService.sendEmail(to, subject, body);
        });

        // Opcional: verificar se a mensagem da exceção é a esperada
        // assertEquals("Gateway failed to send email", thrown.getMessage());

        // Verifica se o método sendEmail do gateway foi chamado
        verify(emailSenderGateway, times(1)).sendEmail(to, subject, body);
    }
}
