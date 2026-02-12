.PHONY: help up down restart ps logs logs-db \
	backend-run backend-build backend-test backend-clean backend-format backend-format-check \
	db-shell db-reset

help:
	@echo "Available commands:"
	@echo "  make up               Start docker compose"
	@echo "  make down             Stop docker compose"
	@echo "  make logs             Follow all logs"
	@echo "  make backend-run      Run Spring Boot"
	@echo "  make backend-test     Run backend tests"
	@echo "  make backend-build    Build backend jar"

# Docker
up:
	docker compose up -d

down:
	docker compose down

restart:
	docker compose down && docker compose up -d

ps:
	docker compose ps

logs:
	docker compose logs -f

logs-db:
	docker compose logs -f db

# Backend
backend-run:
	cd backend && ./mvnw spring-boot:run

backend-build:
	cd backend && ./mvnw clean package

backend-test:
	cd backend && ./mvnw test

backend-clean:
	cd backend && ./mvnw clean

backend-format:
	cd backend && ./mvnw spotless:apply

backend-format-check:
	cd backend && ./mvnw spotless:check

# DB shell
db-shell:
	docker compose exec postgres psql -U $$DB_USER -d $$DB_NAME

db-reset:
	docker compose down -v