# Email Microservice

[![CI - Build, Test and Security Analysis](https://github.com/SamuelDinizTenorio/EMAIL-SERVICE/actions/workflows/ci.yml/badge.svg)](https://github.com/SamuelDinizTenorio/EMAIL-SERVICE/actions/workflows/ci.yml)

Este é um microserviço de exemplo construído com Spring Boot, projetado para enviar e-mails de forma desacoplada e robusta. A aplicação utiliza o Amazon Simple Email Service (SES) como provedor de e-mail e segue princípios de Arquitetura Limpa (Clean Architecture) para uma clara separação de responsabilidades.

## Funcionalidades

- Envio de e-mails via API REST.
- Validação de dados de entrada (destinatário, assunto, corpo).
- Tratamento de erros centralizado e padronizado.
- Logging estruturado em todas as camadas da aplicação.
- Configuração para arquivamento de logs (rolling file).
- Arquitetura desacoplada, permitindo a fácil substituição do provedor de e-mail.
- Containerização com Docker e orquestração com Docker Compose.

## Arquitetura

O projeto é estruturado seguindo os princípios da Arquitetura Limpa/Hexagonal, separando as responsabilidades em diferentes camadas:

- **`core`**: O coração da aplicação. Contém as interfaces dos casos de uso (as "portas de entrada", ex: `EmailSenderUseCase`) e as exceções de domínio.
- **`infrastructure`**: Contém toda a implementação técnica e adaptadores.
    - **`application`**: Implementa os casos de uso definidos no `core` (ex: `EmailSenderService`).
    - **`controller`**: Adaptadores de entrada que expõem a funcionalidade via API REST.
    - **`ses`**: Adaptador de saída (infraestrutura) para o Amazon SES.
    - **`config`**: Configurações do Spring e Beans.
    - **`dto`**: Objetos de Transferência de Dados.
    - **`exception`**: Tratamento global de exceções (`GlobalExceptionHandler`).

## CI/CD Pipeline

Este projeto utiliza GitHub Actions para integração contínua. O pipeline, definido em `.github/workflows/ci.yml`, é acionado a cada `push` ou `pull request` para a branch `main`.

O pipeline executa os seguintes passos:

1.  **Build e Teste:** Compila o código e executa todos os testes de unidade e integração usando Maven.
2.  **Análise de Segurança:** Utiliza o GitHub CodeQL para escanear o código em busca de vulnerabilidades de segurança conhecidas.

Um build bem-sucedido garante que o projeto está compilando, que os testes estão passando e que nenhuma nova vulnerabilidade de segurança foi introduzida.

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.5.8**
- **Maven**
- **Docker & Docker Compose**
- **Amazon Web Services (AWS) SES**: Provedor de envio de e-mails.
- **Lombok**: Para reduzir código boilerplate.
- **JUnit 5, Mockito & AssertJ**: Para testes de unidade e integração robustos.

## Pré-requisitos

- **Docker** e **Docker Compose** instalados.
- Uma conta na **AWS** com acesso ao **SES**.
- **AWS SES em modo Produção** (ou com os e-mails de teste devidamente verificados no modo Sandbox).

## Como Executar (Recomendado)

A forma mais simples e recomendada de executar a aplicação é utilizando Docker Compose.

1. **Clone o repositório:**
   ```sh
   git clone <url-do-repositorio>
   cd email-service
   ```

2. **Crie o arquivo de ambiente:**
   Copie o arquivo de exemplo `.env.example` para um novo arquivo chamado `.env` e preencha com suas credenciais. O arquivo `.env` é ignorado pelo Git para manter seus segredos seguros.
   ```sh
   cp .env.example .env
   ```
   Agora, edite o arquivo `.env` com seus valores.

3. **Suba o serviço com Docker Compose:**
   Este comando irá construir a imagem Docker e iniciar o container em modo "detached" (em segundo plano).
   ```sh
   docker-compose up --build -d
   ```
   A aplicação estará disponível em `http://localhost:8081`.

4. **Visualizando os Logs:**
   Para ver os logs da aplicação em tempo real, use o comando:
   ```sh
   docker-compose logs -f
   ```

5. **Parando o Serviço:**
   Para parar e remover os containers, use o comando:
   ```sh
   docker-compose down
   ```

## Como Executar (Alternativa sem Docker)

Se preferir, você pode executar a aplicação diretamente com Maven.

1. **Instale as dependências:**
   - Java 21 ou superior.
   - Maven 3.8 ou superior.

2. **Exporte as variáveis de ambiente** no seu terminal ou configure-as na sua IDE.

3. **Execute a aplicação:**
   ```sh
   mvn spring-boot:run
   ```

## Documentação da API

### Enviar E-mail

Envia um e-mail para o destinatário especificado.

- **URL**: `/api/email/send`
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
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/email/send",
  "timestamp": "2023-10-27T15:30:00.123456",
  "errors": {
    "to": "Invalid email format"
  }
}
```

#### Resposta de Erro (Exemplo: 500 Internal Server Error)

Se ocorrer uma falha no servidor durante o envio:

```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "Failure while sending email",
  "path": "/api/email/send",
  "timestamp": "2023-10-27T15:35:00.123456"
}
```

#### Resposta de Erro (Exemplo: 405 Method Not Allowed)

Se tentar usar um método HTTP incorreto (ex: GET):

```json
{
  "status": 405,
  "error": "Method Not Allowed",
  "message": "Method Not Allowed. Supported methods: [POST]",
  "path": "/api/email/send",
  "timestamp": "2023-10-27T15:40:00.123456"
}
```

#### Resposta de Erro (Exemplo: 415 Unsupported Media Type)

Se enviar um Content-Type incorreto (ex: application/xml):

```json
{
  "status": 415,
  "error": "Unsupported Media Type",
  "message": "Unsupported Media Type. Please use application/json.",
  "path": "/api/email/send",
  "timestamp": "2023-10-27T15:45:00.123456"
}
```

## Como Executar os Testes

Para rodar todos os testes de unidade e integração, execute o seguinte comando Maven:

```sh
mvn test
```
Um relatório de cobertura de testes pode ser gerado com o plugin JaCoCo (não configurado por padrão).

---

## Troubleshooting

### `UnknownHostException: ...amazonaws.com`

**Sintoma:** Ao tentar enviar um e-mail, a aplicação lança um erro `java.net.UnknownHostException`, indicando que não conseguiu encontrar o endereço do servidor da AWS. Isso geralmente acontece quando o container Docker perde a configuração de DNS da máquina hospedeira.

**Solução:** A solução mais simples é **reiniciar o Docker Desktop**. Isso força a reconfiguração das redes do Docker e geralmente resolve o problema.
