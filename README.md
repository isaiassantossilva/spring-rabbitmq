# spring-rabbitmq

Projeto de estudo de integração **Spring Boot + RabbitMQ**, demonstrando o envio assíncrono de e-mails através de uma fila de mensagens, com persistência em PostgreSQL.

## Arquitetura

```
HTTP POST /emails  ──►  EmailController
                            │
                            ▼
                        EmailService.sendToQueue()
                            │
                            ▼
                     ┌──────────────┐
                     │   RabbitMQ   │   (fila: emails)
                     └──────────────┘
                            │
                            ▼
                        EmailListener
                            │
                            ▼
                        EmailService.sendToRecipient()
                            │
                ┌───────────┴───────────┐
                ▼                       ▼
          EmailRepository          EmailGateway
          (PostgreSQL)             (envio real)
```

O cliente faz uma requisição HTTP, o controller publica a mensagem na fila e retorna imediatamente. Um listener consome de forma assíncrona, persiste o registro e dispara o envio através do gateway, atualizando o status (`PENDING` → `SENT` / `FAILED`).

## Stack

- **Java 21**
- **Spring Boot 4.0.2** (`starter-amqp`, `starter-data-jpa`, `starter-webmvc`)
- **RabbitMQ 3** (imagem `rabbitmq:3-management`)
- **PostgreSQL 17.7**
- **Gradle (Kotlin DSL)**
- **Lombok** + **MapStruct 1.6.3**

## Pré-requisitos

- JDK 21
- Docker + Docker Compose

## Como executar

### 1. Subir infraestrutura (RabbitMQ + Postgres)

```bash
docker compose up -d
```

Serviços expostos:

| Serviço            | Porta  | Credenciais                          |
|--------------------|--------|--------------------------------------|
| RabbitMQ (AMQP)    | 5672   | `spring-rabbitmq` / `spring-rabbitmq`|
| RabbitMQ (UI)      | 15672  | `spring-rabbitmq` / `spring-rabbitmq`|
| PostgreSQL         | 5432   | `spring-rabbitmq` / `spring-rabbitmq`|

Painel de gerenciamento do RabbitMQ: http://localhost:15672

### 2. Rodar a aplicação

```bash
./gradlew bootRun
```

### 3. Enviar um e-mail para a fila

```bash
curl -X POST http://localhost:8080/emails \
  -H "Content-Type: application/json" \
  -d '{
    "recipient": "destinatario@exemplo.com",
    "subject": "Olá",
    "body": "Mensagem de teste"
  }'
```

A requisição retorna imediatamente (`200 OK`). O envio efetivo acontece em background — acompanhe os logs da aplicação ou consulte a tabela `emails`.

## Configuração

A configuração de filas é externalizada em `application.yaml`:

```yaml
rabbitmq:
  queues:
    emails:
      name: emails
      concurrency: 100-2000
```

- `name`: nome da fila declarada em `RabbitMQConfig`.
- `concurrency`: faixa de consumidores simultâneos (`min-max`) aplicada via `@RabbitListener`.

As propriedades são carregadas por `QueueProperty` com `@ConfigurationProperties`.

## Estrutura do projeto

```
src/main/java/com/santos/spring_rabbitmq
├── SpringRabbitmqApplication.java
├── config
│   ├── RabbitMQConfig.java          # Declaração de fila e MessageConverter
│   ├── AuditConfig.java
│   └── property/QueueProperty.java
├── controller/EmailController.java   # Endpoint REST
├── service/EmailService.java         # Publica e consome da fila
├── listener/EmailListener.java       # @RabbitListener
├── gateway/EmailGateway.java         # Simula envio real
├── entity/                           # EmailEntity, BaseEntity
├── repository/EmailRepository.java
├── dto/EmailDTO.java
├── mapper/EmailMapper.java           # MapStruct
└── util/ThreadUtil.java
```

## Testes

```bash
./gradlew test
```

## Parar a infraestrutura

```bash
docker compose down          # preserva os volumes
docker compose down -v       # remove volumes (zera dados)
```
