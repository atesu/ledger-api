Ledger API

A RESTful ledger API built with Java and Spring Boot. The application supports account creation, account balance retrieval, transfers between accounts, idempotency, and transaction event publishing through RabbitMQ.

Technologies

Java 23

Spring Boot

Spring Data JPA and Hibernate

MySQL

Flyway

RabbitMQ

Docker and Docker Compose

Maven

JUnit and Mockito

Features

Create accounts with a specified currency and overdraft limit.

Retrieve account details and current balance.

Transfer funds between accounts of the same currency.

Validate transfer amounts and currency precision.

Enforce overdraft limits.

Use idempotency keys to prevent duplicate transfers.

Record transfers using debit and credit ledger entries.

Publish transaction events to RabbitMQ.

Run the application and RabbitMQ using Docker Compose.

Requirements

Docker Desktop

Java 23 and Maven Wrapper for running tests locally

Run with Docker Compose

Build the application JAR:

./mvnw clean package -DskipTests

On Windows Command Prompt, use:

mvnw.cmd clean package -DskipTests

Build and start the containers:

docker compose up --build -d

Check container status:

docker compose ps

The API is available at http://localhost:8081.

RabbitMQ Management UI is available at http://localhost:15672.

Default local RabbitMQ credentials:

Username: guest

Password: guest

Database Configuration

The Docker Compose configuration connects the API to a MySQL database running on the host.

Default database settings:

Database: ledger

Username: ledger

Password: ledger

The API uses the following environment variables:

DB_URL

DB_USERNAME

DB_PASSWORD

RABBITMQ_HOST

SPRING_RABBITMQ_PORT

Ensure MySQL is running and the database exists before starting the API.

API Endpoints

Create an account

POST /api/v1/accounts

Example request:

{
  "currency": "EUR",
  "overdraftLimit": 10.00
}

Example response:

{
  "id": "9515ac5b-9947-4bfa-81ab-350bc38fda64",
  "currency": "EUR",
  "overdraftLimit": 10.00,
  "balance": 0
}

Supported currencies depend on the AccountCurrency enum.

Get account details

GET /api/v1/accounts/{id}

Returns account details, including the current balance.

Transfer funds

POST /api/v1/transactions

Example request:

{
  "sourceAccountId": "9515ac5b-9947-4bfa-81ab-350bc38fda64",
  "destinationAccountId": "458f039c-202e-4a14-9a7c-2138a90e86b0",
  "amount": 1.00,
  "idempotencyKey": "b7c2e9a1-5d43-4f86-8a21-3c6e0d9f7254"
}

The idempotency key must be a valid UUID. Reusing a key with the same request returns the existing transaction; reusing it with different request data is rejected.

A successful transfer returns the transaction ID.

RabbitMQ Events

Successful transfers publish JSON events to the durable queue:

ledger.transaction.events

Each event contains:

transactionId

sourceAccountId

destinationAccountId

amount

currency

createdAt

The queue can be inspected through the RabbitMQ Management UI.

Run Tests

On Windows:

mvnw.cmd clean test

On Linux or macOS:

./mvnw clean test

Stop the Containers

docker compose down

This stops and removes the Compose containers. It does not automatically remove the MySQL database running on the host.

