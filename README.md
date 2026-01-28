FlashSale Transaction Manager
A high-performance, distributed flash sale system built with Spring Boot 4.0.1, designed to handle extreme concurrency using distributed locking and asynchronous event processing.

🚀 Key Features
Atomic Stock Management: Prevents over-selling using Redisson distributed locks.

Event-Driven Architecture: Orders are published to Apache Kafka for downstream fulfillment.

Containerized Testing: Full integration test suite using Testcontainers for Postgres, Redis, and Kafka.

Kubernetes Ready: Optimized Dockerfile and manifests for scaling in a cluster environment.

🛠 Tech Stack
Java 17 & Spring Boot 4.0.1

PostgreSQL: Persistence for products and orders.

Redis (Redisson): Distributed locking and caching.

Kafka: Asynchronous order event streaming.

Lombok: Reducing boilerplate code.

🏗 System Architecture
The system uses a Lock-Outside-Transaction pattern to ensure that the database commit is finalized before the distributed lock is released, preventing race conditions during high-concurrency spikes.

🚦 Getting Started
Prerequisites
Docker Desktop (with "Allow default Docker socket" enabled)

JDK 17

Maven 3.9+

Local Development (Docker Compose)
The project includes a compose.yml that provides the infrastructure. Spring Boot's Docker Compose support will automatically connect the app to these services.

Bash

docker compose up -d
mvn spring-boot:run
Running Integration Tests
The tests use Testcontainers to spin up isolated environments for Postgres, Redis, and Kafka.

Bash

mvn clean test -Dtest=FlashSaleIntegrationTest
📦 Containerization & Deployment
Build Image
Bash

docker build -t ghcr.io/<your-username>/flashsale:latest .
Push to GitHub Container Registry (GHCR)
Bash

echo $CR_PAT | docker login ghcr.io -u <your-username> --password-stdin
docker push ghcr.io/<your-username>/flashsale:latest
Kubernetes Deployment
If you have a cluster running, you can convert the compose.yml or use the provided manifests:

Bash

kubectl apply -f k8s/
🔍 Critical Fixes Implemented
Transaction/Lock Sync: Refactored code to ensure Redisson locks wrap the @Transactional boundary, preventing "Dirty Reads" of stock levels.

Kafka Serialization: Configured JsonSerializer to handle OrderEvent DTOs correctly across the network.

Test Isolation: Implemented the Singleton Container pattern in Testcontainers for faster, more reliable integration tests.

🗺 Roadmap
[ ] GraphQL Integration: Expose Product Queries and Order Mutations.

[ ] Order Consumer: Implement the listener service to process Kafka events.

[ ] Prometheus/Grafana: Add monitoring for lock contention and Kafka lag.

[ ] Autoscaling: Configure HPA (Horizontal Pod Autoscaler) based on request volume.