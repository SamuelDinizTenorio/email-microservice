package com.Samuel.email_microservice.infrastructure.ses;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração para o cliente do Amazon Simple Email Service (SES).
 * Responsável por criar e configurar o bean do {@link AmazonSimpleEmailService}.
 */
@Slf4j
@Configuration
public class AwsSesConfig {

    private final String accessKey;
    private final String secretKey;
    private final String region;

    /**
     * Construtor que injeta as credenciais e a região da AWS a partir das propriedades da aplicação.
     *
     * @param accessKey A chave de acesso da AWS (aws.accessKeyId).
     * @param secretKey A chave secreta da AWS (aws.secretKey).
     * @param region    A região da AWS (aws.region).
     */
    public AwsSesConfig(@Value("${aws.accessKeyId}") String accessKey,
                        @Value("${aws.secretKey}") String secretKey,
                        @Value("${aws.region}") String region) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
    }

    /**
     * Cria e configura o bean do cliente do AWS SES.
     * Este bean será injetado em outras partes da aplicação, como no {@link AwsSesEmailSender}.
     *
     * @return Uma instância configurada de {@link AmazonSimpleEmailService}.
     */
    @Bean
    public AmazonSimpleEmailService amazonSimpleEmailService() {
        log.info("Configuring Amazon SES client for region: {}", region);
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonSimpleEmailServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }
}
