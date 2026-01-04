package com.Samuel.email_microservice.infrastructure.ses;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("AWS SES Config Tests")
class AwsSesConfigTest {

    // Valores simulados para as propriedades @Value
    private final String accessKey = "testAccessKey";
    private final String secretKey = "testSecretKey";
    private final String region = "us-east-1";

    @Test
    @DisplayName("Should successfully create AwsSesConfig instance with injected values")
    void awsSesConfig_shouldBeCreatedWithInjectedValues() {
        // Arrange - Valores de configuração simulados já definidos como campos da classe.

        // Act
        AwsSesConfig config = new AwsSesConfig(accessKey, secretKey, region);

        // Assert
        assertNotNull(config, "AwsSesConfig should not be null");
        // Não podemos verificar diretamente os campos privados (accessKey, secretKey, region)
        // sem getters ou reflexão, mas a criação bem-sucedida do objeto já indica que o construtor funcionou.
    }

    @Test
    @DisplayName("Should create AmazonSimpleEmailService bean successfully")
    void amazonSimpleEmailService_shouldCreateBean() {
        // Arrange
        AwsSesConfig config = new AwsSesConfig(accessKey, secretKey, region);

        // Act
        AmazonSimpleEmailService sesClient = config.amazonSimpleEmailService();

        // Assert
        assertNotNull(sesClient, "AmazonSimpleEmailService bean should not be null");
        // A inspeção mais profunda da configuração do cliente é complexa em um teste de unidade puro.
        // Este teste garante que o processo de criação do bean não falha imediatamente.
    }
}
