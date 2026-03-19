# E-Commerce Logistics Platform

![CI Pipeline](https://github.com/Sakshi3027/ecommerce-logistics-platform/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?logo=springboot)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.9-black?logo=apachekafka)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
![GCP](https://img.shields.io/badge/Deployed%20on-GCP-4285F4?logo=googlecloud)
![JWT](https://img.shields.io/badge/Auth-JWT-000000?logo=jsonwebtokens)
![License](https://img.shields.io/badge/License-MIT-yellow)
```

A production-grade microservices-based logistics platform built with Java Spring Boot, Apache Kafka, and PostgreSQL. Designed to handle the core backend operations of large-scale e-commerce companies like Walmart, Amazon, and Target.

---

## Live Demo (Deployed on Google Cloud Run)

> **API Gateway:** https://api-gateway-456053639387.us-central1.run.app

### Authentication
All API endpoints are JWT protected. Get a token first:
```bash
curl -X POST https://api-gateway-456053639387.us-central1.run.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Then use the token in all requests:
```bash
curl https://api-gateway-456053639387.us-central1.run.app/api/orders/1 \
  -H "Authorization: Bearer <your-token>"
```

| Endpoint | Method | Description |
|---|---|---|
| /api/auth/login | POST | Get JWT token |
| /api/orders | POST | Create order |
| /api/orders/{id} | GET | Get order by ID |
| /api/inventory/product/{id} | GET | Check stock level |
| /api/inventory | POST | Add inventory |
| /api/recommendations/trending | GET | Get trending products |
| /api/notifications | GET | Get notifications |

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                     E-Commerce Logistics Platform                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ┌──────────────────────────────────────────────────────────┐  │
│   │              API Gateway (Port 8080)                     │  │
│   │         JWT Auth + Rate Limiting (60 req/min)            │  │
│   └──────┬──────────┬──────────┬──────────┬─────────────────┘  │
│          │          │          │          │                      │
│          ▼          ▼          ▼          ▼                      │
│   ┌──────────┐ ┌─────────┐ ┌────────┐ ┌──────────────────┐    │
│   │  Order   │ │Inventory│ │Warehouse│ │    Delivery      │    │
│   │ Service  │ │ Service │ │ Service │ │    Service       │    │
│   │  :8081   │ │  :8082  │ │  :8083  │ │     :8084        │    │
│   └────┬─────┘ └─────────┘ └────────┘ └──────────────────┘    │
│        │                                                         │
│        │ Kafka Events (order-events)                            │
│        ▼                                                         │
│   ┌──────────────────────────────────────────────────────────┐  │
│   │                    Apache Kafka                          │  │
│   │              (Event Streaming Platform)                  │  │
│   └──────┬───────────────────┬──────────────────┬───────────┘  │
│          │                   │                  │               │
│          ▼                   ▼                  ▼               │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐    │
│   │  Delivery    │  │ Notification │  │ Recommendation   │    │
│   │   Service    │  │   Service    │  │    Service       │    │
│   │  Port: 8084  │  │  Port: 8085  │  │   Port: 8086     │    │
│   └──────────────┘  └──────────────┘  └──────────────────┘    │
│                                                                  │
│   Infrastructure:                                                │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐    │
│   │  PostgreSQL  │  │    Redis     │  │    Zookeeper     │    │
│   │  Port: 5432  │  │  Port: 6379  │  │   Port: 2181     │    │
│   └──────────────┘  └──────────────┘  └──────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 + Spring Boot 3.5 | Microservices framework |
| Apache Kafka | Async event streaming between services |
| PostgreSQL 15 | Persistent data storage |
| Redis 7 | Caching layer |
| Docker + Docker Compose | Infrastructure orchestration |
| Spring Cloud Gateway | API Gateway routing |
| Spring Security + JWT | Authentication & Authorization |
| Spring Data JPA + Hibernate | ORM and database access |
| Swagger / OpenAPI 3 | API documentation |
| JUnit 5 + Mockito | Unit and integration testing |
| GitHub Actions | CI/CD Pipeline |
| Google Cloud Run | Cloud deployment |
| Google Cloud SQL | Managed PostgreSQL on GCP |
| Lombok | Boilerplate reduction |
| Maven | Build and dependency management |

---

## Security

The API Gateway implements two layers of security:

**JWT Authentication**
- All routes protected except `/api/auth/**`
- Stateless token-based auth — no sessions
- Tokens expire after 24 hours
- Role-based access (ADMIN, USER)

**Rate Limiting**
- 60 requests per minute per IP address
- Returns `429 Too Many Requests` when exceeded
- Rate limit headers on every response:
  - `X-RateLimit-Limit: 60`
  - `X-RateLimit-Remaining: N`

---

## Microservices

### 1. Order Service (Port 8081)
Handles the full order lifecycle from placement to completion.
- Create and manage orders with multiple line items
- Auto-calculates order totals
- Publishes order events to Kafka on every status change
- Full CRUD with order tracking by customer

**Key APIs:**
```
POST   /api/orders                          → Place new order
GET    /api/orders/{id}                     → Get order by ID
GET    /api/orders/customer/{customerId}    → Customer order history
PUT    /api/orders/{id}/status              → Update order status
PUT    /api/orders/{id}/cancel              → Cancel order
```

### 2. Inventory Service (Port 8082)
Real-time inventory tracking with automatic status management.
- Tracks stock levels per product
- Auto-updates status: IN_STOCK → LOW_STOCK → OUT_OF_STOCK
- Listens to order events to auto-deduct stock
- Low stock and out-of-stock reporting

**Key APIs:**
```
POST   /api/inventory                       → Add product inventory
GET    /api/inventory/product/{productId}  → Check stock level
GET    /api/inventory/low-stock            → Low stock report
PUT    /api/inventory/deduct-stock         → Deduct stock
PUT    /api/inventory/add-stock            → Restock product
```

### 3. Warehouse Service (Port 8083)
Multi-warehouse management with stock transfer capabilities.
- Manage multiple fulfillment centers
- Track products across warehouse locations (shelf/aisle)
- Transfer stock between warehouses
- Capacity tracking per warehouse

**Key APIs:**
```
POST   /api/warehouses                      → Create warehouse
POST   /api/warehouses/products            → Add product to warehouse
POST   /api/warehouses/transfer            → Transfer stock
GET    /api/warehouses/transfers           → Transfer history
```

### 4. Delivery Service (Port 8084)
Driver management and last-mile delivery tracking.
- Register and manage delivery drivers
- Auto-assign best available driver based on rating
- Real-time delivery status tracking
- Driver location updates
- Listens to order events to auto-create deliveries

**Key APIs:**
```
POST   /api/deliveries                      → Create delivery
POST   /api/deliveries/{id}/auto-assign    → Auto assign driver
PUT    /api/deliveries/{id}/status         → Update delivery status
POST   /api/deliveries/drivers             → Register driver
GET    /api/deliveries/drivers/available   → Available drivers
```

### 5. Notification Service (Port 8085)
Event-driven notification engine for email and SMS alerts.
- Listens to ALL Kafka events across the platform
- Sends email notifications for every order status change
- Sends SMS for shipped and delivered orders
- Low stock alerts to warehouse team
- In-memory notification log (extensible to real email providers)

**Key APIs:**
```
GET    /api/notifications                   → View all sent notifications
POST   /api/notifications/low-stock        → Trigger low stock alert
```

### 6. Recommendation Service (Port 8086)
ML-inspired product recommendation engine.
- Tracks product views and purchase history per customer
- Personalized recommendations based on purchase history
- Trending products ranked by engagement score (purchases × 3 + views × 1)
- Similar products via collaborative filtering
- Falls back to trending if no customer history exists

**Key APIs:**
```
GET    /api/recommendations/{customerId}    → Personalized recommendations
GET    /api/recommendations/trending       → Trending products
GET    /api/recommendations/similar/{id}   → Similar products
POST   /api/recommendations/view           → Record product view
```

---

## Event-Driven Architecture

Every order action publishes a Kafka event that automatically triggers reactions across the platform:

```
Customer places order
        │
        ▼
Order Service → publishes "PENDING" event
        │
        ├──▶ Inventory Service  → deducts stock automatically
        ├──▶ Delivery Service   → creates delivery record
        ├──▶ Notification Service → sends "Order Placed" email
        └──▶ Recommendation Service → records purchase data

Order confirmed
        │
        ├──▶ Delivery Service   → assigns best available driver
        └──▶ Notification Service → sends "Order Confirmed" email
```

---

## Testing

12 automated tests across unit and integration layers:

**Unit Tests (JUnit 5 + Mockito)** — test business logic in isolation:
- `getOrderById_WhenOrderExists_ReturnsOrder`
- `getOrderById_WhenOrderNotFound_ThrowsException`
- `updateOrderStatus_WhenOrderExists_UpdatesStatus`
- `cancelOrder_WhenOrderIsPending_CancelsSuccessfully`
- `createOrder_CalculatesTotalCorrectly`

**Integration Tests (MockMvc)** — test full HTTP layer:
- `createOrder_ReturnsCreatedOrder`
- `getOrderById_ReturnsOrder`
- `getOrderById_WhenNotFound_Returns404`
- `updateOrderStatus_ReturnsUpdatedOrder`
- `cancelOrder_ReturnsCancelledOrder`
- `createOrder_WithMissingFields_Returns400`

Run tests:
```bash
cd order-service && mvn test
```

---

## CI/CD Pipeline

Every push to `main` automatically:
1. Spins up PostgreSQL test database on GitHub servers
2. Runs all 12 tests
3. Builds Docker images for all 7 services
4. Reports pass/fail — badge updates in real time

---

## Local Setup

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker + Docker Compose

### 1. Clone the repository
```bash
git clone https://github.com/Sakshi3027/ecommerce-logistics-platform.git
cd ecommerce-logistics-platform
```

### 2. Start entire platform with one command
```bash
docker-compose up
```
This starts PostgreSQL, Kafka, Zookeeper, Redis, all 6 microservices and API Gateway automatically.

### 3. Verify all services are running
```
http://localhost:8081/actuator/health  → Order Service
http://localhost:8082/actuator/health  → Inventory Service
http://localhost:8083/actuator/health  → Warehouse Service
http://localhost:8084/actuator/health  → Delivery Service
http://localhost:8085/actuator/health  → Notification Service
http://localhost:8086/actuator/health  → Recommendation Service
```

---

## API Documentation (Swagger UI)

Interactive API docs available at:
```
http://localhost:8081/swagger-ui/index.html  → Order Service
http://localhost:8082/swagger-ui/index.html  → Inventory Service
http://localhost:8083/swagger-ui/index.html  → Warehouse Service
http://localhost:8084/swagger-ui/index.html  → Delivery Service
```

---

## Sample API Flow

```bash
# 1. Get JWT token
curl -X POST http://localhost:8090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Add inventory
curl -X POST http://localhost:8082/api/inventory \
  -H "Content-Type: application/json" \
  -d '{"productId":101,"productName":"iPhone 15 Pro","quantity":50,"lowStockThreshold":10}'

# 3. Place an order
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "shippingAddress": "123 Main St, New York, NY",
    "items": [{"productId": 101, "productName": "iPhone 15 Pro", "quantity": 1, "unitPrice": 999.99}]
  }'

# 4. Check inventory was auto-deducted (Kafka magic!)
curl http://localhost:8082/api/inventory/product/101

# 5. Register a driver
curl -X POST http://localhost:8084/api/deliveries/drivers \
  -H "Content-Type: application/json" \
  -d '{"name":"John Smith","phone":"555-0101","email":"john@driver.com","vehicleType":"Van","vehiclePlate":"NYC-1234","currentCity":"New York"}'

# 6. Auto-assign driver to delivery
curl -X POST http://localhost:8084/api/deliveries/1/auto-assign

# 7. Check notifications were sent
curl http://localhost:8085/api/notifications

# 8. Get trending products
curl http://localhost:8086/api/recommendations/trending
```

---

## Database Schema

Each service owns its own database (Database-per-Service pattern):

| Service | Database | Key Tables |
|---|---|---|
| Order Service | order_db | orders, order_items |
| Inventory Service | inventory_db | inventory |
| Warehouse Service | warehouse_db | warehouses, warehouse_products, stock_transfers |
| Delivery Service | delivery_db | deliveries, drivers |
| Recommendation Service | recommendation_db | product_views, product_purchases, trending_products |

---

## Key Design Patterns

- **Database per Service** — each microservice owns its data, zero shared databases
- **Event-Driven Architecture** — services communicate via Kafka, fully decoupled
- **SAGA Pattern** — distributed transactions handled through compensating events
- **API Gateway Pattern** — single entry point with JWT auth and rate limiting
- **Repository Pattern** — clean separation between business logic and data access
- **DTO Pattern** — request/response objects keep API contracts stable
- **Builder Pattern** — immutable object construction via Lombok @Builder

---

## Author

**Sakshi** — [GitHub](https://github.com/Sakshi3027)