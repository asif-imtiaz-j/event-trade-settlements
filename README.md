# Event-Driven Trade Settlements

Architecture (free-tier friendly):
- API Gateway (HTTP) -> Ingest service
- Upstash Kafka -> topic per domain (orders, risk, settlements) with DLQs
- Spring Boot microservices (orders, risk, settlements)
- Neon Postgres
- Notifications via Slack webhook
- Observability via Grafana Cloud (OTLP)

Tooling:
- Java 17 + Maven
- GitHub Actions CI
- Testcontainers for integration tests
