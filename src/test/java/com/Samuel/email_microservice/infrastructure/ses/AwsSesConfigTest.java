package com.Samuel.email_microservice.infrastructure.ses;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        var config = new AwsSesConfig(accessKey, secretKey, region);

        // Assert
        assertThat(config)
                .as("AwsSesConfig instance should not be null")
                .isNotNull();
    }

    @Test
    @DisplayName("Should create AmazonSimpleEmailService bean successfully")
    void amazonSimpleEmailService_shouldCreateBean() {
        // Arrange
        var config = new AwsSesConfig(accessKey, secretKey, region);

        // Act
        AmazonSimpleEmailService sesClient = config.amazonSimpleEmailService();

        // Assert
        assertThat(sesClient)
                .as("AmazonSimpleEmailService bean should not be null")
                .isNotNull();
    }
}
