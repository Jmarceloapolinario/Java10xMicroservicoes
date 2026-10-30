# Java10x Microserviços

Projeto desenvolvido durante o curso **Java10x** para praticar a construção de uma arquitetura de **microsserviços** com Java e Spring Boot. Dois serviços independentes se comunicam de forma assíncrona via **RabbitMQ**: ao cadastrar um usuário, o serviço `user` publica um evento e o serviço `email` consome a mensagem e envia um e-mail de boas-vindas.

## Arquitetura

```
                       ┌───────────────────────┐
  POST /users  ──────▶ │  user  (porta 8081)   │
                       │  - salva o usuário    │
                       │  - publica mensagem   │
                       └───────┬───────────────┘
                               │
                      fila "email-queue"
                        (RabbitMQ)
                               │
                       ┌───────▼───────────────┐
                       │  email (porta 8080)   │
                       │  - consome a fila     │
                       │  - envia e-mail (SMTP)│
                       │  - salva o histórico  │
                       └───────────────────────┘
```

Cada serviço possui seu **próprio banco PostgreSQL**, seguindo o princípio de *database per service*.

| Serviço | Porta | Responsabilidade |
|---------|-------|------------------|
| **user**  | `8081` | CRUD de usuários, validação de e-mail duplicado e publicação do evento de boas-vindas na fila. |
| **email** | `8080` | Consome a fila `email-queue`, envia o e-mail via SMTP (Gmail) e registra o envio com status (`PENDING`, `SENT`, `FAILED`, `DELIVERED`). |

## Tecnologias

- **Java 17**
- **Spring Boot 4.0.7** (Web MVC, Data JPA, Validation, AMQP, Mail)
- **RabbitMQ** (conexão com SSL, compatível com serviços como CloudAMQP)
- **PostgreSQL 17**
- **Flyway** (migrations do serviço `user`)
- **springdoc-openapi** (Swagger UI no serviço `user`)
- **Lombok**
- **dotenv** para variáveis de ambiente
- **Docker Compose** (banco de dados de cada serviço)
- **Maven** (wrapper incluso)

## Estrutura do repositório

```
Java10xMicroservicoes/
├── user/
│   ├── src/main/java/com/java10x/user/
│   │   ├── config/        # Conversor JSON do RabbitMQ
│   │   ├── controller/    # UserController
│   │   ├── domain/        # UserModel
│   │   ├── dto/           # UserDto, EmailDto
│   │   ├── exception/     # DuplicateEmailException, GlobalExceptionHandler
│   │   ├── producer/      # UserProducer (publica na fila)
│   │   ├── repository/    # UserRepository
│   │   └── service/       # UserService
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/  # Scripts Flyway (V1, V2)
│   ├── docker-compose.yml
│   └── pom.xml
└── email/
    ├── src/main/java/com/java10x/email/
    │   ├── config/        # Declaração da fila e conversor JSON
    │   ├── consumer/      # EmailConsumer (@RabbitListener)
    │   ├── domain/        # EmailModel
    │   ├── dto/           # EmailDto
    │   ├── enums/         # EmailStatus
    │   ├── repository/    # EmailRepository
    │   └── service/       # EmailService (envio + persistência)
    ├── src/main/resources/application.yml
    ├── docker-compose.yml
    └── pom.xml
```

## Pré-requisitos

- JDK 17+
- Docker e Docker Compose
- Uma instância de RabbitMQ acessível (a configuração atual usa SSL)
- Uma conta Gmail com [senha de app](https://support.google.com/accounts/answer/185833) para o envio de e-mails

## Configuração

Cada serviço lê suas variáveis de um arquivo `.env` na **raiz da própria pasta** (`user/.env` e `email/.env`). Esse arquivo não deve ser versionado.

**`user/.env`**

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=user_db
DB_USER=postgres
DB_PASSWORD=sua_senha

RABBITMQ_HOST=seu-host-rabbitmq
RABBITMQ_PORT=5671
RABBITMQ_USER=seu_usuario
RABBITMQ_PASSWORD=sua_senha
RABBITMQ_VHOST=seu_vhost
```

**`email/.env`**

```env
DB_HOST=localhost
DB_PORT=5435
DB_NAME=email_db
DB_USER=postgres
DB_PASSWORD=sua_senha

RABBITMQ_HOST=seu-host-rabbitmq
RABBITMQ_PORT=5671
RABBITMQ_USER=seu_usuario
RABBITMQ_PASSWORD=sua_senha
RABBITMQ_VHOST=seu_vhost

EMAIL_USERNAME=seu_email@gmail.com
EMAIL_PASSWORD=sua_senha_de_app
```

> O `docker-compose.yml` de cada serviço também usa `DB_USER`, `DB_PASSWORD` e `DB_NAME` do `.env`. O banco do `user` é exposto na porta `5432` e o do `email` na porta `5435`.

## Como executar

1. **Clone o repositório**

   ```bash
   git clone https://github.com/Jmarceloapolinario/Java10xMicroservicoes.git
   cd Java10xMicroservicoes
   ```

2. **Suba o banco e o serviço `user`**

   ```bash
   cd user
   docker compose up -d
   ./mvnw spring-boot:run
   ```

3. **Em outro terminal, suba o banco e o serviço `email`**

   ```bash
   cd email
   docker compose up -d
   ./mvnw spring-boot:run
   ```

> No Windows, use `mvnw.cmd spring-boot:run`.

Ao iniciar, o serviço `email` cria automaticamente a fila durável `email-queue` no RabbitMQ.

## API do serviço `user`

Base URL: `http://localhost:8081`

| Método | Rota | Descrição | Retorno |
|--------|------|-----------|---------|
| `POST` | `/users` | Cria um usuário e dispara o e-mail de boas-vindas | `201 Created` |
| `GET` | `/list/users` | Lista todos os usuários | `200 OK` |
| `GET` | `/users/{email}` | Busca um usuário pelo e-mail | `200 OK` |
| `DELETE` | `/users/{id}` | Remove um usuário pelo UUID | `204 No Content` |

**Exemplo de requisição**

```bash
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Maria Silva", "email": "maria@exemplo.com"}'
```

**Exemplo de resposta**

```json
{
  "userId": "3f6c1a52-8d0e-4c53-9a35-2b8b7a1c9e10",
  "name": "Maria Silva",
  "email": "maria@exemplo.com"
}
```

**Erro ao cadastrar e-mail já existente** (`409 Conflict`)

```json
{
  "status": "CONFLICT",
  "statusCode": 409,
  "message": "Usuário com o email 'maria@exemplo.com' já existe no sistema.",
  "error": "Email já registrado",
  "timestamp": "2026-10-30T14:00:00"
}
```

A documentação interativa (Swagger UI) fica disponível em `http://localhost:8081/swagger-ui.html`.

## Fluxo de mensagens

1. O cliente chama `POST /users` no serviço `user`.
2. O serviço verifica se o e-mail já existe e, se não existir, salva o usuário (tabela `tb_users`).
3. O `UserProducer` publica um `EmailDto` (userId, destinatário, assunto e corpo) na fila `email-queue`.
4. O `EmailConsumer` do serviço `email` recebe a mensagem e chama o `EmailService`.
5. O e-mail é enviado via SMTP e o registro é salvo na tabela `TB_EMAIL` com o status `SENT` ou `FAILED`.

## Testes

```bash
cd user && ./mvnw test
cd email && ./mvnw test
```


## Sobre o curso

Este projeto foi desenvolvido por mim, passo a passo, acompanhando as aulas do curso **Java10x**, como prática dos conceitos de microsserviços, mensageria e integração entre serviços.

## Autor

**José Marcelo**
GitHub: [@Jmarceloapolinario](https://github.com/Jmarceloapolinario)

---

Projeto criado para fins de estudo durante o curso Java10x.
