package com.Samuel.email_service.infrastructure.ses;

import com.Samuel.email_service.core.port.EmailSenderGateway;
import com.Samuel.email_service.core.exception.EmailServiceException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AWS SES Email Sender Tests")
class AwsSesEmailSenderTest {

    @Mock
    private AmazonSimpleEmailService amazonSimpleEmailService;

    private EmailSenderGateway awsSesEmailSender;

    private final String fromEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        // Arrange
        // Instancia AwsSesEmailSender manualmente para injetar o mock e o valor @Value
        awsSesEmailSender = new AwsSesEmailSender(amazonSimpleEmailService, fromEmail);
    }

    @Test
    @DisplayName("Should call Amazon SES client with correct SendEmailRequest")
    void sendEmail_shouldCallAmazonSesClientWithCorrectRequest() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        // Act
        awsSesEmailSender.sendEmail(to, subject, body);

        // Assert
        // Captura o argumento passado para o método sendEmail do mock
        ArgumentCaptor<SendEmailRequest> requestCaptor = ArgumentCaptor.forClass(SendEmailRequest.class);
        verify(amazonSimpleEmailService, times(1)).sendEmail(requestCaptor.capture());

        SendEmailRequest capturedRequest = requestCaptor.getValue();

        // Verifica os detalhes da requisição capturada
        assertEquals(fromEmail, capturedRequest.getSource());
        assertEquals(to, capturedRequest.getDestination().getToAddresses().get(0));
        assertEquals(subject, capturedRequest.getMessage().getSubject().getData());
        assertEquals(body, capturedRequest.getMessage().getBody().getText().getData());
        assertEquals("UTF-8", capturedRequest.getMessage().getSubject().getCharset());
        assertEquals("UTF-8", capturedRequest.getMessage().getBody().getText().getCharset());
    }

    @Test
    @DisplayName("Should throw EmailServiceException when AmazonServiceException occurs")
    void sendEmail_shouldThrowEmailServiceException_whenAmazonServiceExceptionOccurs() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";
        AmazonServiceException amazonException = new AmazonServiceException("AWS SES service error");
        amazonException.setStatusCode(500);

        // Configura o mock para lançar AmazonServiceException quando sendEmail for chamado
        doThrow(amazonException).when(amazonSimpleEmailService).sendEmail(any(SendEmailRequest.class));

        // Act & Assert
        // Verifica se EmailServiceException é lançada
        EmailServiceException thrown = assertThrows(EmailServiceException.class, () -> {
            awsSesEmailSender.sendEmail(to, subject, body);
        });

        // Opcional: verificar a mensagem da exceção e a causa
        assertEquals("Failure while sending email", thrown.getMessage());
        assertEquals(amazonException, thrown.getCause());

        // Verifica se o método sendEmail do cliente AWS foi chamado
        verify(amazonSimpleEmailService, times(1)).sendEmail(any(SendEmailRequest.class));
    }
}
