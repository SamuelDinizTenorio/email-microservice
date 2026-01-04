package com.Samuel.email_microservice.infrastructure.application;

import com.Samuel.email_microservice.core.port.EmailSenderGateway;
import com.Samuel.email_microservice.core.exception.EmailServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        // Simula a mensagem exata que a implementação real (AwsSesEmailSender) lançaria
        var gatewayException = new EmailServiceException("Failure while sending email");

        // Configura o mock para lançar uma exceção quando o método sendEmail for chamado
        doThrow(gatewayException)
                .when(emailSenderGateway).sendEmail(to, subject, body);

        // Verifica se o serviço lança a mesma exceção usando AssertJ
        assertThatThrownBy(() -> emailSenderService.sendEmail(to, subject, body))
                .isInstanceOf(gatewayException.getClass())
                .hasMessage(gatewayException.getMessage());

        // Verifica se o método sendEmail do gateway foi chamado
        verify(emailSenderGateway, times(1)).sendEmail(to, subject, body);
    }
}
