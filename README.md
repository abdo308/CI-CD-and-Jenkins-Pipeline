# Calculator App (Java + React)

A modern calculator application with a Java Spring Boot backend and React frontend, designed to demonstrate CI/CD pipelines with Jenkins.

## Features

- **Basic operations**: add, subtract, multiply, divide, modulo
- **Scientific operations**: power, square root, absolute value, factorial, negate
- **Full keyboard support**
- **Calculation history**
- **REST API backend** with comprehensive unit tests

## Run

1) Build and start everything:

```
docker compose up --build
```

2) Open the frontend:

- http://localhost:5173

## Services

- Calculator API: http://localhost:8080/api/health
- Frontend: http://localhost:5173

## Run Tests

```bash
cd services/calculator
mvn test
```

## API Endpoints

| Method | Path             | Description                          |
|--------|------------------|--------------------------------------|
| GET    | `/api/health`    | Health check                         |
| POST   | `/api/calculate` | Perform a calculation                |

### POST /api/calculate

Request body:
```json
{
  "operation": "add",
  "a": 10,
  "b": 5
}
```

Supported operations: `add`, `subtract`, `multiply`, `divide`, `modulo`, `power`, `sqrt`, `abs`, `negate`, `factorial`

Response:
```json
{
  "operation": "add",
  "a": 10.0,
  "b": 5.0,
  "result": 15.0
}
```

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.3, Maven
- **Frontend**: React 18, Vite 5
- **Testing**: JUnit 5, Spring MockMvc
- **CI/CD**: Jenkins
