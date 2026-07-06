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

A production-grade microservices logistics platform built with Java 21, Spring Boot, Apache Kafka, and PostgreSQL. Designed to handle the core backend operations of large-scale e-commerce systems — from order placement through last-mile delivery.

**Live API Gateway (GCP Cloud Run):** https://api-gateway-456053639387.us-central1.run.app

---

## Quick Start

Get a JWT token:

```bash
curl -X POST https://api-gateway-456053639387.us-central1.run.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Use the token in all requests:

```bash
curl https://api-gateway-456053639387.us-central1.run.app/api/orders/1 \
  -H "Authorization: Bearer "
```

---

## Architecture

The platform runs 6 fully decoupled microservices behind a single API Gateway. Services communicate asynchronously via Apache Kafka — no direct service-to-service calls.
API Gateway (Port 8080)
JWT Auth + Rate Limiting (60 req/min)
│
├── Order Service       :8081
├── Inventory Service   :8082
├── Warehouse Service   :8083
├── Delivery Service    :8084
├── Notification Service :8085
└── Recommendation Service :8086
Infrastructure
├── Apache Kafka        :9092
├── PostgreSQL          :5432
├── Redis               :6379
└── Zookeeper           :2181

**Event flow example:**

When a customer places an order, the Order Service publishes a Kafka event that automatically triggers:
- Inventory Service deducts stock
- Delivery Service creates a delivery record
- Notification Service sends an order confirmation email
- Recommendation Service records the purchase

No polling. No direct calls. Fully event-driven.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Event Streaming | Apache Kafka 3.9 |
| Database | PostgreSQL 15 |
| Caching | Redis 7 |
| Auth | Spring Security + JWT |
| API Gateway | Spring Cloud Gateway |
| ORM | Spring Data JPA + Hibernate |
| API Docs | Swagger / OpenAPI 3 |
| Testing | JUnit 5 + Mockito |
| CI/CD | GitHub Actions |
| Cloud | GCP Cloud Run + Cloud SQL |
| Containers | Docker + Docker Compose |

---

## Microservices

### Order Service — Port 8081
Full order lifecycle from placement to completion. Publishes Kafka events on every status change.
POST   /api/orders                        Create order
GET    /api/orders/{id}                   Get order by ID
GET    /api/orders/customer/{customerId}  Customer order history
PUT    /api/orders/{id}/status            Update order status
PUT    /api/orders/{id}/cancel            Cancel order

### Inventory Service — Port 8082
Real-time stock tracking with automatic status management (IN_STOCK → LOW_STOCK → OUT_OF_STOCK). Listens to order events to auto-deduct stock.
POST   /api/inventory                     Add product inventory
GET    /api/inventory/product/{productId} Check stock level
GET    /api/inventory/low-stock           Low stock report
PUT    /api/inventory/deduct-stock        Deduct stock
PUT    /api/inventory/add-stock           Restock product

### Warehouse Service — Port 8083
Multi-warehouse management with stock transfer capabilities across fulfillment centers.
POST   /api/warehouses                    Create warehouse
POST   /api/warehouses/products           Add product to warehouse
POST   /api/warehouses/transfer           Transfer stock between warehouses
GET    /api/warehouses/transfers          Transfer history

### Delivery Service — Port 8084
Driver management and last-mile delivery tracking. Auto-assigns best available driver by rating.
POST   /api/deliveries                    Create delivery
POST   /api/deliveries/{id}/auto-assign   Auto assign driver
PUT    /api/deliveries/{id}/status        Update delivery status
POST   /api/deliveries/drivers            Register driver
GET    /api/deliveries/drivers/available  Available drivers

### Notification Service — Port 8085
Event-driven notification engine. Listens to all Kafka events across the platform and sends email and SMS alerts.
GET    /api/notifications                 View all sent notifications
POST   /api/notifications/low-stock       Trigger low stock alert

### Recommendation Service — Port 8086
ML-inspired recommendation engine using collaborative filtering and engagement scoring.
GET    /api/recommendations/{customerId}  Personalized recommendations
GET    /api/recommendations/trending      Trending products
GET    /api/recommendations/similar/{id}  Similar products
POST   /api/recommendations/view          Record product view

---

## Security

**JWT Authentication**
- All routes protected except `/api/auth/**`
- Stateless token-based auth, no sessions
- Tokens expire after 24 hours
- Role-based access: ADMIN, USER

**Rate Limiting**
- 60 requests per minute per IP
- Returns `429 Too Many Requests` when exceeded
- Headers on every response: `X-RateLimit-Limit` and `X-RateLimit-Remaining`

---

## Design Patterns

- **Database per Service** — each microservice owns its data, zero shared databases
- **Event-Driven Architecture** — services communicate via Kafka, fully decoupled
- **SAGA Pattern** — distributed transactions handled through compensating events
- **API Gateway Pattern** — single entry point with JWT auth and rate limiting
- **Repository Pattern** — clean separation between business logic and data access
- **DTO Pattern** — stable API contracts via request/response objects
- **Builder Pattern** — immutable object construction via Lombok

---

## Testing

12 automated tests across unit and integration layers.

**Unit Tests (JUnit 5 + Mockito)**
- Order retrieval, status updates, cancellation, total calculation

**Integration Tests (MockMvc)**
- Full HTTP layer including 404 and 400 error handling

```bash
cd order-service && mvn test
```

**CI/CD** — every push to `main` automatically spins up a PostgreSQL test database, runs all 12 tests, builds Docker images for all 7 services, and updates the badge.

---

## Local Setup

**Prerequisites:** Java 21+, Maven 3.9+, Docker + Docker Compose

```bash
git clone https://github.com/Sakshi3027/ecommerce-logistics-platform.git
cd ecommerce-logistics-platform
docker-compose up
```

Verify all services:
http://localhost:8081/actuator/health  → Order Service
http://localhost:8082/actuator/health  → Inventory Service
http://localhost:8083/actuator/health  → Warehouse Service
http://localhost:8084/actuator/health  → Delivery Service
http://localhost:8085/actuator/health  → Notification Service
http://localhost:8086/actuator/health  → Recommendation Service

Swagger UI:
http://localhost:8081/swagger-ui/index.html

---

## Database Schema

Each service owns its own isolated database:

| Service | Database | Key Tables |
|---|---|---|
| Order | order_db | orders, order_items |
| Inventory | inventory_db | inventory |
| Warehouse | warehouse_db | warehouses, warehouse_products, stock_transfers |
| Delivery | delivery_db | deliveries, drivers |
| Recommendation | recommendation_db | product_views, product_purchases, trending_products |

---

## Author
**Sakshi Chavan** — [GitHub](https://github.com/Sakshi3027)
