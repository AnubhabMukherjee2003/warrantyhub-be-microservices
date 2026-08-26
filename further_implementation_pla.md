# WarrantyHub Microservices — Short Implementation Plan

## 1. Final Architecture

Five Spring Boot projects:

```text
eureka-server                 :8761
api-gateway                   :8080

customer-company-service      :8081
product-warranty-purchase-service :8082
service-request-service       :8083
```

Three business microservices, each with its **own database**.

```text
                    API Gateway :8080
                           |
                       Eureka :8761
                           |
          +----------------+----------------+
          |                |                |
          ↓                ↓                ↓
      Customer          Product          Service
      Company           Warranty          Request
      Service           Purchase          Service
          |                |                |
          ↓                ↓                ↓
      Customer DB       Purchase DB      Service DB
```

## 2. Service Boundaries

### Customer & Company Service

Owns:

```text
Company
Customer
```

Also owns:

```text
Authentication
JWT
User/Company roles
```

Does **not** contain Product, Warranty, Purchase or ServiceRequest.

---

### Product/Warranty/Purchase Service

Owns:

```text
Product
Warranty
Purchase
```

Does **not** contain Customer/Company entities.

Needs customer/company information through **OpenFeign**.

---

### Service Request Service

Owns:

```text
ServiceRequest
RequestStatusHistory
```

Stores references:

```text
purchaseId
customerId
companyId
```

Does **not** contain Purchase, Customer, Company, Product or Warranty entities.

---

## 3. Database Separation

Each service gets its own database:

```text
customer-company-db
purchase-db
service-request-db
```

No service directly accesses another service's database.

No cross-service JPA repositories.

---

## 4. Inter-Service Communication

Use:

```text
OpenFeign + Eureka
```

Example:

```text
Service Request Service
        ↓
PurchaseServiceClient
        ↓
     OpenFeign
        ↓
      Eureka
        ↓
Purchase Service
```

Similarly:

```text
Purchase Service
        ↓
CustomerServiceClient
        ↓
     OpenFeign
        ↓
Customer & Company Service
```

Do **not** import another service's:

```text
@Entity
Repository
Service
```

Use API/DTO communication instead.

---

## 5. API Gateway

Gateway is the only public entry point:

```text
Client → Gateway :8080 → Microservice
```

Routes:

```text
/api/auth/**              → Customer Service
/api/companies/**         → Customer Service
/api/customers/**         → Customer Service
/api/purchases/**         → Purchase Service
/api/service-requests/**  → Service Request Service
```

Gateway contains routing only, not business logic.

---

## 6. What to Modify in Current Code

### Customer Service

Keep only:

```text
Company
Customer
CompanyRepository
CustomerRepository
CompanyService
CustomerService
AuthService
JWT/security
```

### Purchase Service

Keep only:

```text
Product
Warranty
Purchase
ProductRepository
WarrantyRepository
PurchaseRepository
PurchaseService
```

Add:

```text
client/
    CustomerServiceClient.java
```

### Service Request Service

Keep only:

```text
ServiceRequest
RequestStatusHistory
ServiceRequestRepository
RequestStatusHistoryRepository
ServiceRequestService
```

Add:

```text
client/
    PurchaseServiceClient.java
```

Remove duplicated models/repositories from the wrong services.

---

## 7. Implementation Order

```text
1. Separate the existing code into the 3 boundaries
2. Separate databases
3. Add Eureka Client to all services
4. Start Eureka Server
5. Add OpenFeign clients
6. Connect Purchase → Customer Service
7. Connect Service Request → Purchase Service
8. Configure API Gateway
9. Test complete flow
```

### Final request flow

```text
Customer Login
      ↓
   Gateway
      ↓
Customer Service
      ↓
     JWT

Company creates Purchase
      ↓
   Gateway
      ↓
Purchase Service
      ↓
OpenFeign → Customer Service

Customer creates Service Request
      ↓
   Gateway
      ↓
Service Request Service
      ↓
OpenFeign → Purchase Service
      ↓
Create Service Request

Company changes status
      ↓
Service Request Service
      ↓
Update ServiceRequest
      ↓
Create RequestStatusHistory
```

This keeps the architecture genuinely microservice-based while staying small enough for your limited timeline. The uploaded implementation already establishes the five-project structure and three database boundaries; the main work now is removing the duplicated domain code and replacing cross-service repository access with HTTP clients.  
