# WarrantyHub — Complete Microservices Implementation Plan

## 1. Final Architecture

WarrantyHub will be divided into **three business microservices** plus **two infrastructure Spring Boot applications**.

```text
                         Angular / Client
                                |
                                v
                       API Gateway :8080
                                |
                                v
                       Eureka Server :8761
                                |
        +-----------------------+-----------------------+
        |                       |                       |
        v                       v                       v
 Customer & Company      Product/Warranty/       Service Request
       Service             Purchase Service          Service
         :8081                   :8082                  :8083
        |                       |                       |
        v                       v                       v
   Customer DB             Purchase DB            Service DB
```

### The five Spring Boot projects

```text
1. eureka-server
2. api-gateway
3. customer-company-service
4. product-warranty-purchase-service
5. service-request-service
```

There are therefore **five Spring Boot applications**, but only **three are business microservices**.

---

# 2. Responsibility / Boundary

## 2.1 Customer & Company Service — `:8081`

### Owns

```text
Company
Customer
```

### Own APIs

```http
POST /api/companies
POST /api/customers

POST /api/auth/login

GET  /api/customers/me
GET  /api/companies/me
```

Future customer/company APIs can be added here.

### Does NOT own

```text
Product
Warranty
Purchase
ServiceRequest
RequestStatusHistory
```

---

# 3. Product, Warranty & Purchase Service — `:8082`

### Owns

```text
Product
Warranty
Purchase
```

### Own APIs

```http
POST /api/purchases

GET /api/purchases/{id}

GET /api/customers/me/products
```

The purchase API can continue doing the bundle operation:

```text
Company JWT
    ↓
Purchase API
    ↓
Customer + Product + Warranty + Purchase
```

The important boundary is that all four records are managed by this service, while Customer and Company identity information belongs to the Customer & Company service.

### Does NOT own

```text
Company
Customer
ServiceRequest
RequestStatusHistory
```

---

# 4. Service Request Service — `:8083`

### Owns

```text
ServiceRequest
RequestStatusHistory
```

### Own APIs

```http
POST /api/service-requests

GET  /api/service-requests/{id}

GET  /api/service-requests/{id}/history

PUT  /api/service-requests/{id}/status

GET  /api/customers/service-requests

GET  /api/company/service-requests
```

### Does NOT own

```text
Company
Customer
Product
Warranty
Purchase
```

Instead, it keeps references such as:

```text
purchaseId
customerId
companyId
```

when required.

It communicates with other services through HTTP rather than importing their JPA entities/repositories.

---

# 5. Eureka Server — `:8761`

Eureka is the **service registry / discovery server**.

It does not contain business logic.

Services register themselves:

```text
Customer & Company Service
          ↓
        Eureka

Purchase Service
          ↓
        Eureka

Service Request Service
          ↓
        Eureka

API Gateway
          ↓
        Eureka
```

Eureka answers the question:

```text
"Where is PURCHASE-SERVICE?"
```

---

# 6. API Gateway — `:8080`

The Gateway is the single external entry point.

```text
Client
  ↓
API Gateway :8080
  ↓
Eureka / Service Discovery
  ↓
Correct microservice
```

Example routes:

```text
/api/auth/**              → Customer & Company Service
/api/companies/**         → Customer & Company Service
/api/customers/**         → Customer & Company Service
/api/purchases/**         → Product/Warranty/Purchase Service
/api/service-requests/**  → Service Request Service
```

The Gateway should initially focus on routing. Do not put business logic inside it.

---

# 7. The Five Projects to Initialize

## Project 1 — Eureka Server

### Name

```text
eureka-server
```

### Type

Spring Boot Maven project.

### Dependencies

```text
Eureka Server
```

### Main class

```text
EurekaServerApplication
```

### Port

```properties
server.port=8761
spring.application.name=eureka-server
```

Enable Eureka Server on the main class.

---

# 8. Project 2 — API Gateway

### Name

```text
api-gateway
```

### Type

Spring Boot Maven project.

### Dependencies

```text
Spring Cloud Gateway
Eureka Discovery Client
```

Use the Spring Cloud release compatible with the Spring Boot version selected for the project.

### Main class

```text
ApiGatewayApplication
```

### Port

```properties
server.port=8080
spring.application.name=api-gateway
```

Configure it as a Eureka client.

---

# 9. Project 3 — Customer & Company Service

### Name

```text
customer-company-service
```

### Dependencies

```text
Spring Web MVC
Spring Data JPA
Spring Security
Validation
H2
JWT
DevTools
Eureka Discovery Client
```

### Main class

```text
CustomerCompanyServiceApplication
```

### Port

```properties
server.port=8081
spring.application.name=customer-company-service
```

### Packages

```text
controller/
service/
repository/
model/
dto/
security/
config/
exception/
```

### Models

```text
Customer
Company
```

### Main responsibilities

```text
Registration
Authentication
JWT creation
Customer identity
Company identity
Role information
```

---

# 10. Project 4 — Product/Warranty/Purchase Service

### Name

```text
product-warranty-purchase-service
```

### Dependencies

```text
Spring Web MVC
Spring Data JPA
Spring Security
Validation
H2
JWT
DevTools
Eureka Discovery Client
```

### Main class

```text
ProductWarrantyPurchaseServiceApplication
```

### Port

```properties
server.port=8082
spring.application.name=product-warranty-purchase-service
```

### Models

```text
Product
Warranty
Purchase
```

### Packages

```text
controller/
service/
repository/
model/
dto/
client/
security/
config/
exception/
```

### Main responsibility

The company creates a purchase bundle:

```text
POST /api/purchases
       ↓
Customer information
       +
Product
       +
Warranty
       +
Purchase
```

Because Customer is owned by another service, the exact cross-service customer creation/lookup flow must be defined before implementation. Do not directly import `CustomerRepository` from the Customer & Company service.

---

# 11. Project 5 — Service Request Service

### Name

```text
service-request-service
```

### Dependencies

```text
Spring Web MVC
Spring Data JPA
Spring Security
Validation
H2
JWT
DevTools
Eureka Discovery Client
```

### Main class

```text
ServiceRequestServiceApplication
```

### Port

```properties
server.port=8083
spring.application.name=service-request-service
```

### Models

```text
ServiceRequest
RequestStatusHistory
```

### Packages

```text
controller/
service/
repository/
model/
dto/
client/
security/
config/
exception/
```

### Main responsibility

```text
Create Service Request
Fetch Service Request
Fetch history
Change status
Customer request list
Company request list
```

---

# 12. Important Microservice Rule

Do NOT do this:

```java
@Autowired
private PurchaseRepository purchaseRepository;
```

inside the Service Request service.

And do not copy:

```java
@Entity
class Purchase { ... }
```

into the Service Request service.

Instead:

```text
Service Request Service
        |
        | HTTP
        v
Purchase Service
```

Use a client and DTO:

```text
client/
└── PurchaseServiceClient.java

dto/
└── PurchaseInfo.java
```

The same principle applies between the Purchase service and Customer/Company service.

---

# 13. Database Boundary

The clean architecture is:

```text
Customer & Company Service
          ↓
      Customer DB

Product/Warranty/Purchase Service
          ↓
       Purchase DB

Service Request Service
          ↓
       Service DB
```

For local development, H2 can be used separately by each service.

Example:

### Customer service

```properties
spring.datasource.url=jdbc:h2:file:./data/customer-company-db
```

### Purchase service

```properties
spring.datasource.url=jdbc:h2:file:./data/purchase-db
```

### Service Request service

```properties
spring.datasource.url=jdbc:h2:file:./data/service-request-db
```

Each service owns its database.

---

# 14. Eureka Registration

Each business service needs Eureka Client configuration.

Conceptually:

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

After starting everything, Eureka should show:

```text
API-GATEWAY
CUSTOMER-COMPANY-SERVICE
PRODUCT-WARRANTY-PURCHASE-SERVICE
SERVICE-REQUEST-SERVICE
```

---

# 15. API Gateway Routing

The Gateway should route using service discovery.

Conceptually:

```text
Client
  |
  +-- /api/auth/** ------------> CUSTOMER-COMPANY-SERVICE
  |
  +-- /api/companies/** -------> CUSTOMER-COMPANY-SERVICE
  |
  +-- /api/customers/** -------> CUSTOMER-COMPANY-SERVICE
  |
  +-- /api/purchases/** -------> PRODUCT-WARRANTY-PURCHASE-SERVICE
  |
  +-- /api/service-requests/** -> SERVICE-REQUEST-SERVICE
```

The client therefore only needs to know:

```text
http://localhost:8080
```

rather than all three service ports.

---

# 16. Inter-Service Communication

## Example: Customer/Product relationship

The Purchase service needs customer information.

Instead of:

```text
Purchase Service
   ↓
CustomerRepository
```

use:

```text
Purchase Service
   ↓
Customer Service Client
   ↓
Eureka
   ↓
Customer & Company Service
   ↓
JSON response
```

## Example: Service Request

Service Request needs to validate a purchase:

```text
Customer
   ↓
POST /api/service-requests
   ↓
Gateway
   ↓
Service Request Service
   ↓
Purchase Service
   ↓
Verify purchase
   ↓
Create Service Request
```

This is where Eureka + HTTP communication becomes useful.

---

# 17. Authentication Architecture

Authentication belongs primarily to the Customer & Company service.

```text
Client
   ↓
POST /api/auth/login
   ↓
Gateway
   ↓
Customer & Company Service
   ↓
JWT
```

Then:

```text
Client
   ↓
Authorization: Bearer <JWT>
   ↓
Gateway
   ↓
Target Service
   ↓
JWT validation + authorization
```

Each service should be able to validate the JWT rather than trusting arbitrary client-provided identity fields.

---

# 18. Business Flow

## Checkpoint 1 — Company Purchase Entry

```text
Company Login
     ↓
JWT
     ↓
POST /api/purchases
     ↓
Purchase Service
     ↓
Customer + Product + Warranty + Purchase
```

Cross-service identity verification/creation is handled through Customer & Company Service APIs.

---

## Checkpoint 2 — Customer Service Request

```text
Customer Login
     ↓
JWT
     ↓
GET /api/customers/me/products
     ↓
Purchase Service
     ↓
Customer's purchases
     ↓
POST /api/service-requests
     ↓
Service Request Service
```

---

## Checkpoint 3 — Status Tracking

```text
Customer
   ↓
View request
   ↓
Service Request Service
   ↓
Current status + history


Company
   ↓
PUT /api/service-requests/{id}/status
   ↓
Service Request Service
   ↓
Update status
   ↓
Create history record
```

---

# 19. Implementation Order

Do not create all business logic simultaneously.

## Phase M1 — Infrastructure

1. Create Eureka Server.
2. Start Eureka on `8761`.
3. Create API Gateway.
4. Register Gateway with Eureka.

## Phase M2 — Customer & Company Service

1. Create project.
2. Move/adapt Customer and Company models.
3. Create repositories.
4. Create DTOs.
5. Create services.
6. Create authentication.
7. Create JWT.
8. Register with Eureka.
9. Test independently.

## Phase M3 — Product/Warranty/Purchase Service

1. Create project.
2. Create Product, Warranty, Purchase models.
3. Create repositories.
4. Create DTOs.
5. Create services.
6. Create Purchase API.
7. Create Customer Service client.
8. Register with Eureka.
9. Test independently.

## Phase M4 — Service Request Service

1. Create project.
2. Create ServiceRequest.
3. Create RequestStatusHistory.
4. Create repositories.
5. Create DTOs.
6. Create services.
7. Create APIs.
8. Create Purchase Service client.
9. Add JWT authorization.
10. Register with Eureka.
11. Test independently.

## Phase M5 — Integration

Connect:

```text
Gateway
   ↓
Eureka
   ↓
Three business services
```

Then test:

```text
Login
 ↓
Purchase
 ↓
Customer products
 ↓
Service request
 ↓
Company request list
 ↓
Status change
 ↓
History
```

---

# 20. What You Should NOT Add Yet

Because the project has a limited timeline, do not add these unless the faculty requires them:

```text
❌ Config Server
❌ Kafka / RabbitMQ
❌ Kubernetes
❌ Distributed tracing
❌ Complex circuit breakers
❌ Separate authentication server
❌ Complex Gateway business logic
```

The architecture already demonstrates the important concepts:

```text
Microservices
Service ownership
Database ownership
REST
HTTP communication
Eureka
Service discovery
API Gateway
JWT authentication
Role-based authorization
```

---

# 21. Final Project Structure

At the top level:

```text
WarrantyHub/
│
├── eureka-server/
│
├── api-gateway/
│
├── customer-company-service/
│
├── product-warranty-purchase-service/
│
└── service-request-service/
```

Each is an independent Maven/Spring Boot project.

---

# 22. Final Architecture Summary

```text
                         Angular
                            |
                            v
                    +---------------+
                    | API Gateway   |
                    |    :8080      |
                    +-------+-------+
                            |
                            v
                    +---------------+
                    | Eureka Server |
                    |    :8761      |
                    +-------+-------+
                            |
          +-----------------+------------------+
          |                 |                  |
          v                 v                  v
 +----------------+ +----------------+ +----------------+
 | Customer &     | | Product/       | | Service        |
 | Company        | | Warranty/      | | Request        |
 | Service :8081  | | Purchase :8082 | | Service :8083  |
 +-------+--------+ +-------+--------+ +-------+--------+
         |                  |                  |
         v                  v                  v
   Customer DB         Purchase DB        Service DB
```

**This is the five-project architecture we will implement: 2 infrastructure projects + 3 business microservices.**
