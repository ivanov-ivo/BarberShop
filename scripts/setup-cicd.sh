#!/bin/bash

# CI/CD Setup Script for BarberShop
# This script sets up the CI/CD environment and configurations

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging functions
log() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
    exit 1
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

# Check if we're in the right directory
if [ ! -f "pom.xml" ]; then
    error "pom.xml not found. Please run this script from the project root."
fi

log "Setting up CI/CD for BarberShop project..."

# Create necessary directories
log "Creating necessary directories..."
mkdir -p .github/workflows
mkdir -p scripts
mkdir -p docs
mkdir -p backups

# Make scripts executable
log "Making scripts executable..."
chmod +x scripts/*.sh 2>/dev/null || true

# Check if git is initialized
if [ ! -d ".git" ]; then
    warning "Git repository not initialized. Initializing..."
    git init
    git add .
    git commit -m "Initial commit: BarberShop project setup"
fi

# Check if .gitignore exists
if [ ! -f ".gitignore" ]; then
    log "Creating .gitignore file..."
    cat > .gitignore << EOF
# Compiled class file
*.class

# Log file
*.log

# BlueJ files
*.ctxt

# Mobile Tools for Java (J2ME)
.mtj.tmp/

# Package Files #
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar

# virtual machine crash logs
hs_err_pid*
replay_pid*

# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iws
*.iml
*.ipr
.vscode/
*.swp
*.swo
*~

# OS
.DS_Store
.DS_Store?
._*
.Spotlight-V100
.Trashes
ehthumbs.db
Thumbs.db

# Docker
.dockerignore

# Logs
logs/
*.log

# Runtime data
pids/
*.pid
*.seed
*.pid.lock

# Coverage directory used by tools like istanbul
coverage/

# nyc test coverage
.nyc_output

# Dependency directories
node_modules/

# Optional npm cache directory
.npm

# Optional REPL history
.node_repl_history

# Output of 'npm pack'
*.tgz

# Yarn Integrity file
.yarn-integrity

# dotenv environment variables file
.env

# Backup files
backups/
*.backup

# Temporary files
tmp/
temp/
EOF
fi

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    warning "Docker is not installed. Please install Docker to use the CI/CD pipeline."
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    warning "Docker Compose is not installed. Please install Docker Compose to use the CI/CD pipeline."
fi

# Check if Maven wrapper is executable
if [ -f "mvnw" ]; then
    chmod +x mvnw
    log "Maven wrapper is ready"
else
    warning "Maven wrapper not found. Please ensure mvnw is present."
fi

# Install git hooks
log "Setting up git hooks..."
if [ -d ".git" ]; then
    if [ -f "scripts/pre-commit.sh" ]; then
        cp scripts/pre-commit.sh .git/hooks/pre-commit
        chmod +x .git/hooks/pre-commit
        log "Pre-commit hook installed"
    fi
else
    warning "Git repository not found. Skipping git hooks setup."
fi

# Create environment file template
if [ ! -f ".env.template" ]; then
    log "Creating environment template..."
    cat > .env.template << EOF
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/barbershop
SPRING_DATASOURCE_USERNAME=barbershop
SPRING_DATASOURCE_PASSWORD=barbershop

# Application Configuration
SPRING_PROFILES_ACTIVE=docker
BRANCHES=Shumen,Sofia,Plovdiv

# File Upload Configuration
SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=10MB
SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=10MB

# JPA Configuration
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect
SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL=true

# Server Configuration
SERVER_PORT=8080

# Logging Configuration
LOGGING_LEVEL_COM_EXAMPLE_BARBERSHOP=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=DEBUG
EOF
fi

# Create docker-compose override file
if [ ! -f "docker-compose.override.yml" ]; then
    log "Creating docker-compose override file..."
    cat > docker-compose.override.yml << EOF
version: '3.8'

services:
  barbershop-app:
    environment:
      - SPRING_PROFILES_ACTIVE=dev
    volumes:
      - ./logs:/app/logs
    ports:
      - "8080:8080"
      - "5005:5005"  # Debug port
    command: ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar"]

  mysql:
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=rootpassword
      - MYSQL_DATABASE=barbershop
      - MYSQL_USER=barbershop
      - MYSQL_PASSWORD=barbershop
EOF
fi

# Create development environment file
if [ ! -f ".env.dev" ]; then
    log "Creating development environment file..."
    cat > .env.dev << EOF
# Development Environment Configuration
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/barbershop
SPRING_DATASOURCE_USERNAME=barbershop
SPRING_DATASOURCE_PASSWORD=barbershop
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
LOGGING_LEVEL_COM_EXAMPLE_BARBERSHOP=DEBUG
EOF
fi

# Create production environment file template
if [ ! -f ".env.prod" ]; then
    log "Creating production environment file template..."
    cat > .env.prod << EOF
# Production Environment Configuration
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/barbershop
SPRING_DATASOURCE_USERNAME=barbershop
SPRING_DATASOURCE_PASSWORD=barbershop
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_SHOW_SQL=false
LOGGING_LEVEL_COM_EXAMPLE_BARBERSHOP=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=WARN
EOF
fi

# Create Makefile for common tasks
if [ ! -f "Makefile" ]; then
    log "Creating Makefile..."
    cat > Makefile << EOF
# BarberShop Makefile
# Common development and deployment tasks

.PHONY: help build test clean run deploy stop logs

help: ## Show this help message
	@echo "Available commands:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'

build: ## Build the application
	./mvnw clean package

test: ## Run tests
	./mvnw test

clean: ## Clean build artifacts
	./mvnw clean
	docker-compose down -v
	docker system prune -f

run: ## Run the application locally
	./mvnw spring-boot:run

run-docker: ## Run the application with Docker
	docker-compose up -d

deploy: ## Deploy the application
	./scripts/deploy.sh deploy

stop: ## Stop the application
	docker-compose down

logs: ## Show application logs
	docker-compose logs -f

status: ## Show application status
	docker-compose ps

setup: ## Setup CI/CD environment
	./scripts/setup-cicd.sh

check: ## Run code quality checks
	./mvnw checkstyle:check
	./mvnw spotbugs:check

coverage: ## Generate code coverage report
	./mvnw jacoco:report

security: ## Run security scan
	./mvnw org.owasp:dependency-check-maven:check

update-deps: ## Update dependencies
	./mvnw versions:use-latest-releases
	./mvnw versions:commit

EOF
fi

# Create README for CI/CD
if [ ! -f "README-CICD.md" ]; then
    log "Creating CI/CD README..."
    cat > README-CICD.md << EOF
# BarberShop CI/CD Setup

This document provides quick setup instructions for the CI/CD pipeline.

## Quick Start

1. **Setup CI/CD Environment:**
   \`\`\`bash
   ./scripts/setup-cicd.sh
   \`\`\`

2. **Run Tests:**
   \`\`\`bash
   make test
   \`\`\`

3. **Build Application:**
   \`\`\`bash
   make build
   \`\`\`

4. **Run with Docker:**
   \`\`\`bash
   make run-docker
   \`\`\`

5. **Deploy:**
   \`\`\`bash
   make deploy
   \`\`\`

## Available Commands

Run \`make help\` to see all available commands.

## GitHub Actions

The CI/CD pipeline includes:
- Automated testing
- Code quality checks
- Security scanning
- Docker image building
- Automated deployment
- Auto-commit functionality

## Environment Files

- \`.env.template\` - Template for environment variables
- \`.env.dev\` - Development environment
- \`.env.prod\` - Production environment template

## Docker Compose

- \`docker-compose.yml\` - Main configuration
- \`docker-compose.override.yml\` - Development overrides

## Scripts

- \`scripts/deploy.sh\` - Deployment script
- \`scripts/pre-commit.sh\` - Pre-commit hook
- \`scripts/setup-cicd.sh\` - CI/CD setup script

## Documentation

See \`docs/CI_CD_GUIDE.md\` for detailed documentation.
EOF
fi

# Test the setup
log "Testing the setup..."

# Test Maven wrapper
if [ -f "mvnw" ]; then
    log "Testing Maven wrapper..."
    ./mvnw --version
fi

# Test Docker
if command -v docker &> /dev/null; then
    log "Testing Docker..."
    docker --version
fi

# Test Docker Compose
if command -v docker-compose &> /dev/null; then
    log "Testing Docker Compose..."
    docker-compose --version
fi

# Final instructions
log "CI/CD setup completed successfully!"
echo ""
info "Next steps:"
echo "1. Configure GitHub repository secrets if using GitHub Actions"
echo "2. Review and customize environment files (.env.*)"
echo "3. Test the deployment: make run-docker"
echo "4. Run code quality checks: make check"
echo "5. See available commands: make help"
echo ""
info "For detailed documentation, see docs/CI_CD_GUIDE.md"
echo ""
warning "Remember to:"
echo "- Set up GitHub repository secrets for production deployment"
echo "- Review and customize environment variables"
echo "- Test the pipeline in a development environment first"
