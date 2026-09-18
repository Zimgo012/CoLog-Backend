# CoLog

CoLog is a collaborative diary and document API. It supports user authentication, shared diaries, documents with revision history, real-time collaboration, and chat.

## Stack

- Java 21
- Spring Boot 4
- Spring MVC, Spring Security, Spring Data JPA, and Spring WebSocket
- PostgreSQL
- Redis
- JWT authentication
- Maven
- Docker Compose
- Resilience4j

## Project structure

```text
.
├── src/
│   ├── main/
│   │   ├── java/com/zimgo/colog/
│   │   │   ├── auth/           # authentication and security
│   │   │   ├── chat/           # diary chat
│   │   │   ├── config/         # application configuration
│   │   │   ├── diary/          # diary API
│   │   │   ├── document/       # document API and revisions
│   │   │   ├── messages/       # WebSocket message handling
│   │   │   ├── user/           # user API
│   │   │   └── CoLogApplication.java
│   │   └── resources/          # application configuration
│   └── test/                   # application tests
├── Dockerfile
├── compose.yaml                # Redis service for local
└── pom.xml                     # Maven dependencies and build configuration
```

## Run locally

Set the required environment variables (`DATABASE_URI`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `REDIS_URL`, `JWT_SECRET`, `FRONTEND_URL`, `BREVO_API_KEY`, and `BREVO_SENDER_EMAIL`), then run:

```bash
./mvnw spring-boot:run
```
