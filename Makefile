DB       = logistics_db
USER     = logistics_user
PASSWORD = logistics_secure_password
COMPOSE  = docker compose
EXEC     = $(COMPOSE) exec -T postgres psql -U $(USER) -d $(DB)
MVNW     = ./mvnw

.PHONY: help up down destroy psql logs status test build run

help:            ## Show this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) \
	 | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-12s\033[0m %s\n", $$1, $$2}'

up:              ## Start Postgres (waits until it is ready)
	$(COMPOSE) up -d
	@echo "Waiting for Postgres to be healthy..."
	@until [ "$$(docker inspect -f '{{.State.Health.Status}}' logistics-postgres)" = "healthy" ]; do sleep 1; done
	@echo "Ready. Run 'make psql' to connect."

down:            ## Stop Postgres (keeps your data)
	$(COMPOSE) down

destroy:         ## Stop Postgres AND delete all data (fresh start)
	$(COMPOSE) down -v

psql:            ## Open an interactive psql shell inside the container
	$(COMPOSE) exec postgres psql -U $(USER) -d $(DB)

logs:            ## Tail the database logs
	$(COMPOSE) logs -f postgres

status:          ## Show container status
	$(COMPOSE) ps

# Maven shortcuts for the logistics-connect project
test:            ## Run tests globally or for a specific module (e.g., make test MODULE=shipment-service)
ifdef MODULE
	$(MVNW) clean test -pl $(MODULE)
else
	$(MVNW) clean test
endif

build:           ## Build the project without running tests
ifdef MODULE
	$(MVNW) clean install -DskipTests -pl $(MODULE)
else
	$(MVNW) clean install -DskipTests
endif

run:             ## Run a specific Spring Boot microservice (e.g., make run MODULE=shipment-service)
ifdef MODULE
	$(MVNW) spring-boot:run -pl $(MODULE)
else
	@echo "Error: Please specify a MODULE to run. Example: make run MODULE=shipment-service"
endif