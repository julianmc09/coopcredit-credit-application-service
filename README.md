# CoopCredit - Integrated Credit Application System

This project implements a microservice for managing credit applications, developed as part of the Riwi Module 6.1 performance test. The system adheres to **Hexagonal Architecture** principles to ensure a clean, modular, and scalable design.

## Architecture

The system consists of two microservices:

1.  **`credit-application-service` (This project):** The main service that manages affiliates, credit applications, and business logic.
2.  **`risk-central-mock-service`:** A simulated microservice that provides credit risk evaluation.

### Hexagonal Architecture Diagram

Below is a diagram representing the Hexagonal Architecture implemented in the `credit-application-service`.

```
[------------------------------------------------------------------------------------]
|                                     INFRASTRUCTURE                                 |
|                                                                                    |
|  +-----------------------+         +-----------------------+         +-----------------------+  |
|  |   Driving Adapters    |         |      Application      |         |    Driven Adapters    |  |
|  | (Input Adapters)      |         |       (Core)          |         |    (Output Adapters)  |  |
|  +-----------------------+         +-----------------------+         +-----------------------+  |
|  | - REST Controllers    | ------> | - Input Ports (Use Cases) | ------> | - Output Ports        |  |
|  |   - AffiliateController |         |   - RegisterAffiliate     |         |   - AffiliateRepositoryPort | ------> | - JPA Adapters        |
|  |   - AuthController      |         |   - RegisterCreditApp     |         |   - CreditAppRepositoryPort |         |   - AffiliateJpaAdapter |
|  |   - CreditAppController |         |   - EvaluateCreditApp     |         |   - UserRepositoryPort      |         |   - ...               |
|  |                       |         |   - ...                   |         |   - RiskEvaluationPort      | ------> | - HTTP Client         |
|  |                       |         |                       |         |                       |         |   - RiskCentralAdapter  |
|  |                       |         +-----------------------+         |                       |         |                       |
|  |                       |         |        DOMAIN         |         |                       |         |                       |
|  |                       |         |  - Affiliate          |         |                       |         |                       |
|  |                       |         |  - CreditApplication  |         |                       |         |                       |
|  |                       |         |  - User, Role, etc.   |         |                       |         |                       |
|  |                       |         +-----------------------+         |                       |         |                       |
|  +-----------------------+                                           +-----------------------+  |
|                                                                                    |
[------------------------------------------------------------------------------------]
```

## Technologies Used

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Web**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL** (managed by Docker)
- **Flyway** (for database migrations)
- **Docker & Docker Compose**
- **MapStruct** (for DTO and entity mapping)
- **Lombok**
- **JWT (JSON Web Tokens)**
- **Springdoc OpenAPI (Swagger UI)**
- **Actuator & Micrometer** (for observability)

## Setup and Running Instructions

### Prerequisites

- Docker and Docker Compose installed.
- Git.
- JDK 17 (for local development).
- Maven (for local development).

### Running with Docker Compose (Recommended)

1.  **Clone the repositories:**
    Ensure both `credit-application-service` and `risk-central-mock-service` are in the same parent directory.

    ```sh
    git clone <your-main-repo-url>
    git clone <your-mock-repo-url>
    ```

2.  **Navigate to the project directory:**
    ```sh
    cd coopcredit-credit-application-service
    ```

3.  **Bring up the services:**
    ```sh
    docker-compose up --build
    ```
    This command will build the images for both services and start all three containers (`db`, `risk-central-mock-service`, `credit-application-service`).

4.  **Access the API:**
    - **Swagger UI:** `http://localhost:8080/swagger-ui.html`
    - **Actuator Health:** `http://localhost:8080/actuator/health`

### Running Locally (Alternative)

1.  **Start the database:**
    You can use the `docker-compose.yml` to start only the database:
    ```sh
    docker-compose up -d db
    ```
    This will start a PostgreSQL container on port `5432`.

2.  **Configure `application.properties`:**
    Ensure that the database credentials in `src/main/resources/application.properties` point to `localhost:5432`.

3.  **Run the `risk-central-mock-service`:**
    Start the mock microservice from its own project (it should run on port `8081`).

4.  **Run the `credit-application-service`:**
    From the root of this project, execute:
    ```sh
    mvn spring-boot:run
    ```

## API Endpoints

Full API documentation is available in Swagger UI.

### Authentication (`/auth`)
| Method | Endpoint         | Description                | Access  |
|--------|------------------|----------------------------|---------|
| POST   | `/auth/register` | Registers a new user.      | Public  |
| POST   | `/auth/login`    | Logs in and obtains a JWT. | Public  |

### Affiliates (`/affiliates`)
| Method | Endpoint                   | Description                  | Required Role |
|--------|----------------------------|------------------------------|---------------|
| POST   | `/`                        | Registers a new affiliate.   | ADMIN         |
| GET    | `/{id}`                    | Retrieves an affiliate by ID. | ADMIN, ANALYST |
| GET    | `/document/{document}`     | Retrieves an affiliate by document. | ADMIN, ANALYST |
| GET    | `/`                        | Retrieves all affiliates.    | ADMIN, ANALYST |
| PUT    | `/{id}`                    | Updates an affiliate.        | ADMIN         |

### Credit Applications (`/credit-applications`)
| Method | Endpoint               | Description                     | Required Role |
|--------|------------------------|---------------------------------|---------------|
| POST   | `/`                    | Registers a new application.    | AFFILIATE     |
| POST   | `/{id}/evaluate`       | Evaluates a pending application. | ANALYST       |
| GET    | `/{id}`                | Retrieves an application by ID. | AFFILIATE, ANALYST, ADMIN |
| GET    | `/`                    | Retrieves all applications.     | ADMIN         |
| GET    | `/pending`             | Retrieves pending applications. | ANALYST       |

## Roles and Workflow

### User Roles
- **`ROLE_AFILIADO`**: Can register credit applications and view the status of their own applications.
- **`ROLE_ANALISTA`**: Can view and evaluate credit applications that are in `PENDING` status.
- **`ROLE_ADMIN`**: Has full access to all system information and functionalities.

### Credit Application Workflow
1.  A user with `ROLE_AFILIADO` authenticates and sends a `POST` request to `/credit-applications` to register a new application.
2.  The system creates the application with `PENDING` status.
3.  A user with `ROLE_ANALISTA` authenticates and can view pending applications via `GET /credit-applications/pending`.
4.  The analyst sends a `POST` request to `/credit-applications/{id}/evaluate` to initiate the evaluation.
5.  The system:
    a. Calls the `risk-central-mock-service` to obtain a `score` and `riskLevel`.
    b. Applies internal policies (quota/income ratio, maximum amount, minimum affiliation time).
    c. Decides if the application is `APPROVED` or `REJECTED` and records the reason.
    d. Updates the application's status and associates it with the risk evaluation.
6.  The affiliate can check the final status of their application.
