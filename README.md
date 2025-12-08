# Email Service

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

- **`core`**: O coração da aplicação. Contém as interfaces dos casos de uso (as "portas de entrada", ex: `EmailSenderUseCase`).
- **`application`**: Implementa os casos de uso definidos no `core`.
- **`adapters`**: Define as interfaces para a comunicação com o mundo exterior (as "portas de saída", ex: `EmailSenderGateway`).
- **`infra`**: Contém as implementações concretas (adaptadores de saída) para as portas definidas em `adapters`. Ex: `AwsSesEmailSender`.
- **`controllers`**: Contém os adaptadores de entrada que expõem a funcionalidade da aplicação via API REST.
- **`dto`**: Data Transfer Objects usados para a comunicação entre as camadas e na API.

## CI/CD Pipeline

Este projeto utiliza GitHub Actions para integração contínua. O pipeline, definido em `.github/workflows/ci.yml`, é acionado a cada `push` ou `pull request` para a branch `main`.

O pipeline executa os seguintes passos:

1.  **Build e Teste:** Compila o código e executa todos os testes de unidade e integração usando Maven.
2.  **Análise de Segurança:** Utiliza o GitHub CodeQL para escanear o código em busca de vulnerabilidades de segurança conhecidas.

Um build bem-sucedido garante que o projeto está compilando, que os testes estão passando e que nenhuma nova vulnerabilidade de segurança foi introduzida.

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3**
- **Maven**
- **Docker & Docker Compose**
- **Amazon Web Services (AWS) SES**: Provedor de envio de e-mails.
- **Lombok**: Para reduzir código boilerplate.
- **JUnit 5 & Mockito**: Para testes de unidade e integração.

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
   A aplicação estará disponível em `http://localhost:8080`.

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
Um relatório de cobertura de testes pode ser gerado com o plugin JaCoCo (não configurado por padrão).

---

## Troubleshooting

### `UnknownHostException: ...amazonaws.com`

**Sintoma:** Ao tentar enviar um e-mail, a aplicação lança um erro `java.net.UnknownHostException`, indicando que não conseguiu encontrar o endereço do servidor da AWS. Isso geralmente acontece quando o container Docker perde a configuração de DNS da máquina hospedeira.

**Solução:** A solução mais simples é **reiniciar o Docker Desktop**. Isso força a reconfiguração das redes do Docker e geralmente resolve o problema.
```