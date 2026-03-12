# 🛒 E-Commerce Logistics Platform

A production-grade **microservices-based logistics platform** built with Java Spring Boot, Apache Kafka, and PostgreSQL. Designed to handle the core backend operations of large-scale e-commerce companies like Walmart, Amazon, and Target.

---

## Architecture Overview
```
┌─────────────────────────────────────────────────────────────────┐
│                     E-Commerce Logistics Platform                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ┌──────────────┐     ┌──────────────┐     ┌───────────────┐  │
│   │ Order Service│────▶│  Inventory   │     │  Warehouse    │  │
│   │  Port: 8081  │     │   Service    │     │   Service     │  │
│   │              │     │  Port: 8082  │     │  Port: 8083   │  │
│   └──────┬───────┘     └──────────────┘     └───────────────┘  │
│          │                                                       │
│          │ Kafka Events (order-events)                          │
│          ▼                                                       │
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
| Spring Data JPA + Hibernate | ORM and database access |
| Swagger / OpenAPI 3 | API documentation |
| Lombok | Boilerplate reduction |
| Maven | Build and dependency management |

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

### 2. Start infrastructure
```bash
docker-compose up -d
```
This starts PostgreSQL, Kafka, Zookeeper, and Redis.

### 3. Start all services (each in a separate terminal)
```bash
cd order-service && mvn spring-boot:run
cd inventory-service && mvn spring-boot:run
cd warehouse-service && mvn spring-boot:run
cd delivery-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd recommendation-service && mvn spring-boot:run
```

### 4. Verify all services are running
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
# 1. Add inventory
curl -X POST http://localhost:8082/api/inventory \
  -H "Content-Type: application/json" \
  -d '{"productId":101,"productName":"iPhone 15 Pro","quantity":50,"lowStockThreshold":10}'

# 2. Place an order
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "shippingAddress": "123 Main St, New York, NY",
    "items": [{"productId": 101, "productName": "iPhone 15 Pro", "quantity": 1, "unitPrice": 999.99}]
  }'

# 3. Check inventory was auto-deducted (Kafka magic!)
curl http://localhost:8082/api/inventory/product/101

# 4. Register a driver
curl -X POST http://localhost:8084/api/deliveries/drivers \
  -H "Content-Type: application/json" \
  -d '{"name":"John Smith","phone":"555-0101","email":"john@driver.com","vehicleType":"Van","vehiclePlate":"NYC-1234","currentCity":"New York"}'

# 5. Auto-assign driver to delivery
curl -X POST http://localhost:8084/api/deliveries/1/auto-assign

# 6. Check notifications were sent
curl http://localhost:8085/api/notifications

# 7. Get trending products
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
- **Repository Pattern** — clean separation between business logic and data access
- **DTO Pattern** — request/response objects keep API contracts stable
- **Builder Pattern** — immutable object construction via Lombok @Builder

---

## Author
**Sakshi** 
[GitHub](https://github.com/Sakshi3027)