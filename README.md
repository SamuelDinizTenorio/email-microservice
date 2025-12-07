# Email Service

Este é um microserviço de exemplo construído com Spring Boot, projetado para enviar e-mails de forma desacoplada e robusta. A aplicação utiliza o Amazon Simple Email Service (SES) como provedor de e-mail e segue princípios de Arquitetura Limpa (Clean Architecture) para uma clara separação de responsabilidades.

## Funcionalidades

- Envio de e-mails via API REST.
- Validação de dados de entrada (destinatário, assunto, corpo).
- Tratamento de erros centralizado e padronizado.
- Logging estruturado em todas as camadas da aplicação.
- Configuração para arquivamento de logs (rolling file).
- Arquitetura desacoplada, permitindo a fácil substituição do provedor de e-mail.

## Arquitetura

O projeto é estruturado seguindo os princípios da Arquitetura Limpa/Hexagonal, separando as responsabilidades em diferentes camadas:

- **`core`**: O coração da aplicação. Contém as interfaces dos casos de uso (as "portas de entrada", ex: `EmailSenderUseCase`).
- **`application`**: Implementa os casos de uso definidos no `core`.
- **`adapters`**: Define as interfaces para a comunicação com o mundo exterior (as "portas de saída", ex: `EmailSenderGateway`).
- **`infra`**: Contém as implementações concretas (adaptadores de saída) para as portas definidas em `adapters`. Ex: `AwsSesEmailSender`.
- **`controllers`**: Contém os adaptadores de entrada que expõem a funcionalidade da aplicação via API REST.
- **`dto`**: Data Transfer Objects usados para a comunicação entre as camadas e na API.

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3**
- **Maven**
- **Amazon Web Services (AWS) SES**: Provedor de envio de e-mails.
- **Lombok**: Para reduzir código boilerplate.
- **JUnit 5 & Mockito**: Para testes de unidade e integração.

## Pré-requisitos

- **Java 21** ou superior.
- **Maven 3.8** ou superior.
- Uma conta na **AWS** com acesso ao **SES**.
- **AWS SES em modo Produção** (ou com os e-mails de teste devidamente verificados no modo Sandbox).

## Configuração

Para rodar a aplicação, você precisa configurar as seguintes variáveis de ambiente. Você pode criar um arquivo `.env` na raiz do projeto ou configurar as variáveis diretamente no seu sistema.

```bash
# Credenciais da AWS
AWS_ACCESS_KEY_ID=SUA_ACCESS_KEY
AWS_SECRET_ACCESS_KEY=SUA_SECRET_KEY
AWS_REGION=sua-regiao-da-aws # ex: us-east-1

# E-mail de remetente verificado no AWS SES
EMAIL_USERNAME=seu-email-verificado@exemplo.com
```

O arquivo `application.yaml` já está configurado para ler essas variáveis.

## Como Executar

1. **Clone o repositório:**
   ```sh
   git clone <url-do-repositorio>
   cd email-service
   ```

2. **Compile o projeto com Maven:**
   ```sh
   mvn clean install
   ```

3. **Execute a aplicação:**
   ```sh
   mvn spring-boot:run
   ```
A aplicação estará disponível em `http://localhost:8080`.

## Documentação da API

### Enviar E-mail

Envia um e-mail para o destinatário especificado.

- **URL**: `/api/email`
- **Método**: `POST`
- **Content-Type**: `application/json`

#### Corpo da Requisição (Exemplo)

```json
{
  "to": "destinatario@exemplo.com",
  "subject": "Assunto do E-mail",
  "body": "Este é o corpo da mensagem."
}
```

#### Resposta de Sucesso (200 OK)

```
Email sent successfully
```

#### Resposta de Erro (Exemplo: 400 Bad Request)

Se a validação falhar (ex: e-mail inválido):

```json
{
  "status": 400,
  "message": "Validation failed: {to=Invalid email format}",
  "timestamp": "2023-10-27T15:30:00.123456"
}
```

#### Resposta de Erro (Exemplo: 500 Internal Server Error)

Se ocorrer uma falha no servidor durante o envio:

```json
{
  "status": 500,
  "message": "Failure while sending email",
  "timestamp": "2023-10-27T15:35:00.123456"
}
```

## Como Executar os Testes

Para rodar todos os testes de unidade e integração, execute o seguinte comando Maven:

```sh
mvn test
```
