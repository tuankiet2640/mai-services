# MAI Microservices Architecture

A secure and scalable microservices architecture built with Spring Boot, focusing on user management and authentication.

## Services

- **User Service**: Manages user data and CRUD operations
- **Auth Service**: Handles authentication and authorization
- **API Gateway**: Routes and secures incoming requests
- **Discovery Server**: Service registration and discovery

## Technology Stack

- **Framework**: Spring Boot
- **Service Discovery**: Netflix Eureka
- **Database**: PostgreSQL
- **Cache**: Redis
- **Security**: JWT, Spring Security
- **Documentation**: OpenAPI/Swagger
- **Monitoring**: Prometheus, Grafana
- **Logging**: ELK Stack
- **Containerization**: Docker, Docker Compose
- **Testing**: JUnit 5, Testcontainers

## Prerequisites

- Java 17+
- Docker and Docker Compose
- Maven

## Project Structure

```
mai-services/
├── discovery-server/     # Eureka Server
├── api-gateway/          # Spring Cloud Gateway
├── auth-service/        # Authentication Service
├── user-service/        # User Management Service
├── docker/              # Docker configurations
├── docker-compose.yml   # Service orchestration
└── .env                 # Environment variables
```

## Getting Started

1. Clone the repository
2. Copy `.env.example` to `.env` and configure your environment variables
3. Run `docker-compose up -d`
4. Access services:
   - Discovery Server: http://localhost:8761
   - API Gateway: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html

## Security

- All sensitive data is encrypted
- Passwords are hashed using BCrypt
- JWT tokens with RSA256 signing
- Role-Based Access Control (RBAC)
- Rate limiting at API Gateway
- HTTPS enabled via NGINX reverse proxy

## Development

### Building Services
```bash
./mvnw clean package
```

### Running Tests
```bash
./mvnw verify
```

### API Documentation
- Swagger UI available at `/swagger-ui.html`
- OpenAPI specs at `/v3/api-docs`

## Monitoring

- Prometheus metrics at `/actuator/prometheus`
- Grafana dashboards for visualization
- ELK Stack for log aggregation

## License

This project is licensed under the MIT License - see the LICENSE file for details.
