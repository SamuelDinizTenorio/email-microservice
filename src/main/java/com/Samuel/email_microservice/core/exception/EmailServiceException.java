package com.Samuel.email_microservice.core.exception;

/**
 * Exceção customizada para representar erros que ocorrem dentro do serviço de e-mail.
 * Esta exceção é usada para encapsular erros específicos da aplicação, como falhas no envio
 * ou problemas de configuração, abstraindo os detalhes de implementação das camadas superiores.
 */
public class EmailServiceException extends RuntimeException {

    /**
     * Construtor que aceita uma mensagem de erro.
     *
     * @param message A mensagem detalhando a causa da exceção.
     */
    public EmailServiceException(String message) {
        super(message);
    }

    /**
     * Construtor que aceita uma mensagem de erro e a causa original.
     * Útil para encapsular exceções de bibliotecas de terceiros (como do AWS SDK)
     * sem expor detalhes de infraestrutura para o resto da aplicação.
     *
     * @param message A mensagem detalhando a causa da exceção.
     * @param cause   A exceção original que foi capturada.
     */
    public EmailServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
