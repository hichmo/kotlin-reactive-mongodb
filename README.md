# Kotlin Spring Boot App Reactive MongoDB

The goal of this application is to provide a template for kotlin reactive mongodb project getting started with Renault Digital stack.

## Build

### Prerequisites

Ensure you have Java 11 available in your environment

### Build command

```
./gradlew clean build
```

## Run locally

### Start the needed services

The following command will start the MongoDB database.
```
cd docker
docker-compose up -d
```

Run the application without building an archive 
```
./gradlew bootRun
```

### Access the application

http://localhost:8080/cars

### Api documentation

http://localhost:8080/swagger-ui.html

