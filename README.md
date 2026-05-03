# Microservices Starter (Java + React)

This is a minimal multi-service starter with Java auth and API services, a database, and a React frontend.

## Run

1) Build and start everything:

```
docker compose up --build
```

2) Open the frontend:

- http://localhost:5173

## Services

- Auth: http://localhost:8081/health
- API: http://localhost:8082/health
- Postgres: localhost:5432

## Demo Login

- Email: demo@local.test
- Password: password123

## Notes

- Tokens are simple HMAC strings for demo use only.
- Users are stored with plaintext passwords for simplicity. Replace with hashing for real use.
