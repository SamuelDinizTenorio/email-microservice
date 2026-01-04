package com.Samuel.email_microservice.infrastructure.ses;

import com.Samuel.email_microservice.core.port.EmailSenderGateway;
import com.Samuel.email_microservice.core.exception.EmailServiceException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        // Verifica os detalhes da requisição capturada usando AssertJ e satisfies
        assertThat(capturedRequest).satisfies(req -> {
            assertThat(req.getSource()).isEqualTo(fromEmail);
            assertThat(req.getDestination().getToAddresses()).containsExactly(to);
            assertThat(req.getMessage().getSubject().getData()).isEqualTo(subject);
            assertThat(req.getMessage().getSubject().getCharset()).isEqualTo("UTF-8");
            assertThat(req.getMessage().getBody().getText().getData()).isEqualTo(body);
            assertThat(req.getMessage().getBody().getText().getCharset()).isEqualTo("UTF-8");
        });
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
        // Verifica se EmailServiceException é lançada usando AssertJ
        assertThatThrownBy(() -> awsSesEmailSender.sendEmail(to, subject, body))
                .isInstanceOf(EmailServiceException.class)
                .hasMessage("Failure while sending email")
                .hasCause(amazonException);

        // Verifica se o método sendEmail do cliente AWS foi chamado
        verify(amazonSimpleEmailService, times(1)).sendEmail(any(SendEmailRequest.class));
    }
}
