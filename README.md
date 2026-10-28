# Ledger API

A Spring Boot double-entry ledger service backed by MySQL, with REST endpoints for accounts, transactions, and reversals, and RabbitMQ integration for payment-settlement messages and transaction events.

## Technology stack

- Java 23 and Spring Boot 4.1.2
- Spring Data JPA / Hibernate
- MySQL and Flyway migrations
- RabbitMQ / Spring AMQP
- Maven Wrapper, JUnit, and Docker


## REST API

Base URL when using the current Docker port mapping: `http://localhost:8081`.

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/v1/accounts` | Create an account |
| `GET` | `/api/v1/accounts/{id}` | Retrieve an account and its balance |
| `POST` | `/api/v1/transactions` | Post a balanced double-entry transaction |
| `GET` | `/api/v1/transactions/{transactionId}` | Retrieve a transaction |
| `POST` | `/api/v1/transactions/{transactionId}/reversals` | Reverse an original transaction |

### Create an account

```http
POST /api/v1/accounts
Content-Type: application/json

{
  "currency": "EUR",
  "overdraftLimit": 100.00
}
```

### Retrieve an account

```http
GET /api/v1/accounts/{id}
```

### Transaction and reversal behavior

A transaction request contains at least two entries, each with an account ID, a direction (`DEBIT` or `CREDIT`), and a positive amount. The transaction must balance before any account balances are changed.

```http
GET /api/v1/transactions/{transactionId}
POST /api/v1/transactions/{transactionId}/reversals
```

The reversal endpoint creates a new transaction with the directions inverted and links it to the original transaction. Repeating a reversal is rejected.

> Consult the request DTOs in `src/main/java/com/ledger/ledger_api/transaction/` for the exact transaction POST JSON schema; do not assume an undocumented field name.

## RabbitMQ integration

RabbitMQ is used for incoming payment-settlement messages and outgoing transaction notifications.

### Incoming payment-settlement queue

- Main queue: `ledger.payment.settled`
- Dead-letter queue: `ledger.payment.settled.dlq`
- RabbitMQ Management UI: `http://localhost:15672` (local development credentials: `guest` / `guest`)

Example message:

```json
{
  "eventId": "e1a3a35e-7a22-46e4-98ab-2a2a9b6c3d31",
  "paymentId": "e1a3a35e-7a22-46e4-98ab-2a2a9b6c3d32",
  "merchantAccountId": "210e6cf1-423a-42cf-a7a3-01274fc70a3d",
  "amount": 1.00,
  "currency": "EUR"
}
```


## Local build and tests

From Windows Command Prompt in the project directory:

```cmd
cd /d D:\projects\ledger-api
.\mvnw.cmd test
```

The last confirmed Maven run reported **7 tests, 0 failures, 0 errors** before the subsequent revert. Run the command again on the final submitted revision and update this statement with the actual result.

## Docker and infrastructure

The current development setup exposes the API at `http://localhost:8081` and RabbitMQ Management at `http://localhost:15672`.

```cmd
docker compose up -d --build
```

The application container uses a Java 23 runtime. In the observed setup, MySQL runs outside the Compose stack and is reached from the application container through `host.docker.internal:3306`; RabbitMQ is reached through the Compose service name `rabbitmq`. Ensure the MySQL database and credentials match the application configuration before starting.

Flyway manages the database schema. The previously verified application startup validated the existing migrations and connected to both MySQL and RabbitMQ.

## Verification performed

| Scenario | Observed result |
|---|---|
| Maven unit tests | **Passed** — 7 tests, 0 failures/errors on the last confirmed run before revert |
| Create EUR account | **Passed** — REST account creation verified |
| Read account | **Passed** — account API exercised |
| Post balanced EUR transaction | **Passed** — transaction created and retrievable |
| Retry with same transaction idempotency key | **Passed** — returned the same transaction ID |
| Reverse a transaction | **Passed** — separate transaction created with inverted entries |
| Attempt a second reversal | **Passed** — rejected with `Transaction has already been reversed` |
| Application startup with MySQL/Flyway | **Passed** — connection and migration validation observed |
| RabbitMQ connection and consumer startup | **Passed** — broker connection established and payment queue had an active consumer |
| Dead-letter queue visibility | **Observed** — rejected messages were visible in `ledger.payment.settled.dlq` |
| Valid EUR payment settlement end to end | **Not yet passed** — processing reached ledger logic but settlement debit was rejected by overdraft validation |
| Final revision after revert | **Not yet reverified** — rerun tests and manual checks |


## Architecture and reliability notes

- The relational database is the source of truth for account balances and ledger entries.
- Database transactions provide atomicity for a ledger posting.
- Pessimistic row locks, acquired in a consistent account-ID order, protect concurrent updates across service instances sharing the same database.
- Transaction idempotency and payment-ID deduplication serve different purposes: the former protects REST retries, while the latter protects broker redelivery and duplicate payment events.
- Failed payment processing should not mark a payment as successfully processed.
- RabbitMQ delivery is asynchronous and can be repeated; consumers must remain idempotent.

## Improvements

1. Complete and test the settlement-account policy and automatic account initialization.
2. Add Testcontainers-based MySQL and RabbitMQ integration tests, including concurrent writers and invalid-message flows.
3. Use a transactional outbox for durable, retryable transaction-event publishing.
4. Add structured tracing and correlation IDs (`eventId`, `paymentId`, transaction ID).
5. Add ledger-entry pagination, running balances, and historical balance queries.
6. Provide a single verified script that builds, starts infrastructure, and runs integration tests.

## AI assistance disclosure

AI assistance was used for implementation guidance, debugging, test planning, and documentation drafting. Generated suggestions require review and validation against the actual source code and executed tests; the verification table distinguishes observed behavior from unfinished work.
