# Quantity Measurement Application

A comprehensive microservices-based application for quantity measurement and conversion, built with Spring Boot and Angular.

## Overview

This project implements a distributed system using microservices architecture to handle quantity measurements and conversions. The application provides RESTful APIs for managing measurements, user authentication, and data persistence.

## Architecture

The application follows a microservices architecture with the following components:

- **Eureka Server**: Service discovery and registration
- **API Gateway**: Centralized routing and load balancing
- **Authentication Service**: User management, JWT token generation, and OAuth2 integration
- **Quantity Service**: Core business logic for quantity measurements and conversions
- **Angular Frontend**: User interface for interacting with the measurement services

## Technology Stack

### Backend (Microservices)
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Cloud 2023.0.0**
- **Spring Security** with JWT authentication
- **Spring Data JPA** for data persistence
- **H2 Database** (development) / **MySQL** (production)
- **Netflix Eureka** for service discovery
- **Spring Cloud Gateway** for API routing
- **OpenFeign** for inter-service communication
- **SpringDoc OpenAPI** for API documentation

### Frontend
- **Angular 17**
- **TypeScript 5.2**
- **RxJS** for reactive programming

### Build Tools
- **Maven** for Java project management
- **npm** for frontend dependency management

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Node.js 18+ and npm
- MySQL (for production profile)

## Getting Started

### Clone the Repository

```bash
git clone <repository-url>
cd quantitymeasurement
```

### Backend Setup

1. **Start Eureka Server**
   ```bash
   cd eureka-server
   mvn spring-boot:run
   ```

2. **Start Authentication Service**
   ```bash
   cd authentication-sevice
   mvn spring-boot:run
   ```

3. **Start Quantity Service**
   ```bash
   cd quantity-service
   mvn spring-boot:run
   ```

4. **Start API Gateway**
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```

### Frontend Setup

1. **Install Dependencies**
   ```bash
   cd QuantityMeasurement-Frontend
   npm install
   ```

2. **Start Development Server**
   ```bash
   npm start
   ```

The frontend will be available at `http://localhost:4200`

## Configuration

### Application Profiles

The application supports multiple profiles:

- **dev** (default): Uses H2 in-memory database
- **prod**: Uses MySQL database

To run with a specific profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Database Configuration

For production, update the MySQL connection details in `application-prod.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/quantity_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## API Documentation

Once the services are running, API documentation is available via Swagger UI:

- **API Gateway**: `http://localhost:8080/swagger-ui.html`
- **Authentication Service**: `http://localhost:8081/swagger-ui.html`
- **Quantity Service**: `http://localhost:8082/swagger-ui.html`

## Testing

Run tests for individual services:

```bash
# Backend tests
mvn test

# Frontend tests
cd QuantityMeasurement-Frontend
npm test
```

## Building for Production

### Backend
```bash
mvn clean package -Dspring-boot.run.profiles=prod
```

### Frontend
```bash
cd QuantityMeasurement-Frontend
npm run build:prod
```

## Deployment

The application is designed to run in a containerized environment. Each microservice can be deployed independently with proper service discovery configuration.

## Features

- User authentication and authorization with JWT
- Quantity measurement conversions
- RESTful API design
- Service discovery and load balancing
- Centralized API gateway
- Database abstraction with JPA
- Comprehensive test coverage
- OpenAPI documentation

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For questions or issues, please create an issue in the repository or contact the development team.