# WebAppUseCases

This repository showcases practical backend use cases and solutions to common challenges in modern distributed systems.
Each module focuses on a different aspect of backend engineering such as concurrency control, caching strategies, memory management, and database connection optimization.

## Requirements
- Java 21
- Podman or Docker (to run Redis and MySQL containers)
  - in this documentation we refer to podman, but docker can be used as well
  - see `./podman.sh` to see the commands used to run the containers

## Run
- Configure the application via `src/main/resources/application.properties`
- Build: `./mvnw clean package -DskipTests`
- Run: `./mvnw spring-boot:run`

## Technical documentation
See `docs/` for detailed explanations of design choices and implementations for each part.

## Further Improvements
- Unit test coverage

## API Documentation
This application uses Swagger UI for API documentation and interaction for easy demonstration.

### Access Swagger UI
- Once the application is running, navigate to:  
  [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
