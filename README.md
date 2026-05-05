# spring-notify-engine

A Spring Boot notification service I built to practice building REST APIs with PostgreSQL persistence and state tracking.

## Why I built this

I wanted hands-on experience with Spring Data JPA, modeling state transitions in a relational database, and structuring a layered Spring Boot application. A notification service felt like a realistic use case — it has a clear request model, persistence requirements, and something to track over time (delivery status).

The service accepts a notification request, saves it to PostgreSQL, dispatches it synchronously, and returns the result.

## Tech Stack

- Java 21 + Spring Boot 3.3.5
- PostgreSQL — stores notification events with status tracking
- Redis — included as a dependency for later use
- Docker Compose — runs postgres, redis, and the app locally
- GitHub Actions — runs tests on every push

## How to Run

```bash
# start postgres and redis
docker-compose up postgres redis

# run the app
./mvnw spring-boot:run

# submit a notification
curl -X POST http://localhost:8080/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "idempotencyKey": "order-001",
    "recipient": "user@example.com",
    "type": "EMAIL",
    "payload": "Your booking is confirmed."
  }'

# run tests
./mvnw test
```
