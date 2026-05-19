# =============================================================================
# SeleniumFramework — short aliases for everyday tasks
# Cross-platform: works on Mac/Linux directly. On Windows use Git Bash.
# =============================================================================
SHELL := /usr/bin/env bash

GRID_URL ?= http://localhost:4444/wd/hub
COMPOSE  := docker compose -f docker/docker-compose.yml

.PHONY: help doctor test smoke regression parallel headless test-one \
        grid-up grid-down grid-status grid-logs grid-ui grid-test \
        report report-static docker-build docker-run clean

# ─── Default ───────────────────────────────────────────────────────────────
help:  ## Show this help
	@printf "\033[1mSeleniumFramework — make targets\033[0m\n\n"
	@awk 'BEGIN {FS = ":.*?## "} \
	     /^[a-zA-Z0-9_-]+:.*?##/ { printf "  \033[36m%-16s\033[0m %s\n", $$1, $$2 }' $(MAKEFILE_LIST)
	@printf "\nExamples:\n"
	@printf "  make doctor                       # check environment\n"
	@printf "  make smoke                        # smoke suite, headless\n"
	@printf "  make test-one T=LoginTests        # run a single class\n"
	@printf "  make test-one T=LoginTests#loginSuccess\n"
	@printf "  make grid-up && make grid-test    # run against local Grid\n"

# ─── Environment ───────────────────────────────────────────────────────────
doctor:  ## Check JDK / Maven / Docker / Chrome
	@echo "Checking environment..."
	@java -version 2>&1 | head -1 | grep -qE '"(21|22|23)\.' && echo "  ✓ Java OK" || echo "  ✗ Need Java 21+"
	@mvn -version >/dev/null 2>&1 && echo "  ✓ Maven OK" || echo "  ✗ Maven not installed"
	@docker --version >/dev/null 2>&1 && echo "  ✓ Docker OK (optional)" || echo "  ℹ Docker not installed (only needed for Grid)"
	@(google-chrome --version 2>/dev/null || chrome --version 2>/dev/null) >/dev/null && echo "  ✓ Chrome OK" || echo "  ℹ Chrome optional (WebDriverManager handles drivers)"
	@test -f pom.xml && echo "  ✓ pom.xml found" || echo "  ✗ Run from repo root"

# ─── Test execution ────────────────────────────────────────────────────────
test:  ## Run default suite (testng.xml)
	mvn clean test

smoke:  ## Run smoke suite only
	mvn test "-Dsurefire.suiteXmlFile=testng-smoke.xml"

regression:  ## Run regression group
	mvn test "-Dgroups=regression"

parallel:  ## Run parallel suite (testng-parallel.xml)
	mvn test "-Dsurefire.suiteXmlFile=testng-parallel.xml"

headless:  ## Run default suite headless
	mvn test "-Dheadless=true"

T ?=
test-one:  ## Run a single test: make test-one T=ClassName[#methodName]
	@if [ -z "$(T)" ]; then echo "Usage: make test-one T=ClassName[#methodName]"; exit 1; fi
	mvn test "-Dtest=$(T)"

# ─── Selenium Grid (Docker) ────────────────────────────────────────────────
grid-up:  ## Start local Selenium Grid (hub + chrome + firefox)
	$(COMPOSE) up -d

grid-down:  ## Stop local Selenium Grid
	$(COMPOSE) down

grid-status:  ## Show Grid container status
	$(COMPOSE) ps

grid-logs:  ## Tail Grid hub logs
	$(COMPOSE) logs -f selenium-hub

grid-ui:  ## Open Grid dashboard in browser
	@(xdg-open $(GRID_URL) 2>/dev/null || open $(GRID_URL) 2>/dev/null || start $(GRID_URL) 2>/dev/null) || echo "Open manually: $(GRID_URL)"

grid-test:  ## Run tests against running Grid
	mvn test "-DgridUrl=$(GRID_URL)"

# ─── Reports ───────────────────────────────────────────────────────────────
report:  ## Serve Allure report (opens browser)
	mvn allure:serve

report-static:  ## Generate static HTML Allure report
	mvn allure:report
	@echo "Static report at: target/site/allure-maven-plugin/index.html"

# ─── Docker test runner ────────────────────────────────────────────────────
docker-build:  ## Build test runner image
	docker build -t selenium-framework -f docker/Dockerfile .

docker-run:  ## Run smoke suite inside Docker
	docker run --rm -v "$$(pwd)/target:/app/target" selenium-framework

# ─── Cleanup ───────────────────────────────────────────────────────────────
clean:  ## mvn clean + remove logs/, reports/, screenshots/
	mvn clean
	@rm -rf logs reports screenshots allure-results 2>/dev/null || true
	@echo "Cleaned: target/, logs/, reports/, screenshots/, allure-results/"
