# CI/CD Setup Script for BarberShop (PowerShell)
# This script sets up the CI/CD environment and configurations

param(
    [switch]$Force
)

# Set error action preference
$ErrorActionPreference = "Stop"

# Colors for output
$Red = "Red"
$Green = "Green"
$Yellow = "Yellow"
$Blue = "Blue"

# Logging functions
function Write-Log {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor $Green
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor $Red
    exit 1
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor $Yellow
}

function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor $Blue
}

# Check if we're in the right directory
if (-not (Test-Path "pom.xml")) {
    Write-Error "pom.xml not found. Please run this script from the project root."
}

Write-Log "Setting up CI/CD for BarberShop project..."

# Create necessary directories
Write-Log "Creating necessary directories..."
$directories = @(".github\workflows", "scripts", "docs", "backups")
foreach ($dir in $directories) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }
}

# Check if git is initialized
if (-not (Test-Path ".git")) {
    Write-Warning "Git repository not initialized. Initializing..."
    git init
    git add .
    git commit -m "Initial commit: BarberShop project setup"
}

# Check if .gitignore exists
if (-not (Test-Path ".gitignore")) {
    Write-Log "Creating .gitignore file..."
    $gitignoreContent = @"
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
"@
    $gitignoreContent | Out-File -FilePath ".gitignore" -Encoding UTF8
}

# Check if Docker is installed
try {
    docker --version | Out-Null
    Write-Log "Docker is available"
} catch {
    Write-Warning "Docker is not installed. Please install Docker to use the CI/CD pipeline."
}

# Check if Docker Compose is installed
try {
    docker-compose --version | Out-Null
    Write-Log "Docker Compose is available"
} catch {
    Write-Warning "Docker Compose is not installed. Please install Docker Compose to use the CI/CD pipeline."
}

# Check if Maven wrapper is executable
if (Test-Path "mvnw.cmd") {
    Write-Log "Maven wrapper is ready"
} else {
    Write-Warning "Maven wrapper not found. Please ensure mvnw.cmd is present."
}

# Create environment file template
if (-not (Test-Path ".env.template")) {
    Write-Log "Creating environment template..."
    $envTemplate = @"
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
"@
    $envTemplate | Out-File -FilePath ".env.template" -Encoding UTF8
}

# Create docker-compose override file
if (-not (Test-Path "docker-compose.override.yml")) {
    Write-Log "Creating docker-compose override file..."
    $dockerOverride = @"
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
"@
    $dockerOverride | Out-File -FilePath "docker-compose.override.yml" -Encoding UTF8
}

# Create development environment file
if (-not (Test-Path ".env.dev")) {
    Write-Log "Creating development environment file..."
    $envDev = @"
# Development Environment Configuration
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/barbershop
SPRING_DATASOURCE_USERNAME=barbershop
SPRING_DATASOURCE_PASSWORD=barbershop
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
LOGGING_LEVEL_COM_EXAMPLE_BARBERSHOP=DEBUG
"@
    $envDev | Out-File -FilePath ".env.dev" -Encoding UTF8
}

# Create production environment file template
if (-not (Test-Path ".env.prod")) {
    Write-Log "Creating production environment file template..."
    $envProd = @"
# Production Environment Configuration
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/barbershop
SPRING_DATASOURCE_USERNAME=barbershop
SPRING_DATASOURCE_PASSWORD=barbershop
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_SHOW_SQL=false
LOGGING_LEVEL_COM_EXAMPLE_BARBERSHOP=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=WARN
"@
    $envProd | Out-File -FilePath ".env.prod" -Encoding UTF8
}

# Create PowerShell script for common tasks
if (-not (Test-Path "scripts\common-tasks.ps1")) {
    Write-Log "Creating PowerShell script for common tasks..."
    $commonTasks = @"
# BarberShop Common Tasks (PowerShell)
# Common development and deployment tasks

param(
    [Parameter(Position=0)]
    [string]`$Command = "help"
)

function Show-Help {
    Write-Host "Available commands:" -ForegroundColor Green
    Write-Host "  build     - Build the application" -ForegroundColor Yellow
    Write-Host "  test      - Run tests" -ForegroundColor Yellow
    Write-Host "  clean     - Clean build artifacts" -ForegroundColor Yellow
    Write-Host "  run       - Run the application locally" -ForegroundColor Yellow
    Write-Host "  run-docker - Run the application with Docker" -ForegroundColor Yellow
    Write-Host "  deploy    - Deploy the application" -ForegroundColor Yellow
    Write-Host "  stop      - Stop the application" -ForegroundColor Yellow
    Write-Host "  logs      - Show application logs" -ForegroundColor Yellow
    Write-Host "  status    - Show application status" -ForegroundColor Yellow
    Write-Host "  check     - Run code quality checks" -ForegroundColor Yellow
    Write-Host "  coverage  - Generate code coverage report" -ForegroundColor Yellow
    Write-Host "  security  - Run security scan" -ForegroundColor Yellow
    Write-Host "  update-deps - Update dependencies" -ForegroundColor Yellow
}

function Build-Application {
    Write-Host "Building application..." -ForegroundColor Green
    .\mvnw.cmd clean package
}

function Test-Application {
    Write-Host "Running tests..." -ForegroundColor Green
    .\mvnw.cmd test
}

function Clean-Application {
    Write-Host "Cleaning build artifacts..." -ForegroundColor Green
    .\mvnw.cmd clean
    docker-compose down -v
    docker system prune -f
}

function Run-Application {
    Write-Host "Running application locally..." -ForegroundColor Green
    .\mvnw.cmd spring-boot:run
}

function Run-Docker {
    Write-Host "Running application with Docker..." -ForegroundColor Green
    docker-compose up -d
}

function Deploy-Application {
    Write-Host "Deploying application..." -ForegroundColor Green
    .\scripts\deploy.bat deploy
}

function Stop-Application {
    Write-Host "Stopping application..." -ForegroundColor Green
    docker-compose down
}

function Show-Logs {
    Write-Host "Showing application logs..." -ForegroundColor Green
    docker-compose logs -f
}

function Show-Status {
    Write-Host "Showing application status..." -ForegroundColor Green
    docker-compose ps
}

function Check-CodeQuality {
    Write-Host "Running code quality checks..." -ForegroundColor Green
    .\mvnw.cmd checkstyle:check
    .\mvnw.cmd spotbugs:check
}

function Generate-Coverage {
    Write-Host "Generating code coverage report..." -ForegroundColor Green
    .\mvnw.cmd jacoco:report
}

function Run-SecurityScan {
    Write-Host "Running security scan..." -ForegroundColor Green
    .\mvnw.cmd org.owasp:dependency-check-maven:check
}

function Update-Dependencies {
    Write-Host "Updating dependencies..." -ForegroundColor Green
    .\mvnw.cmd versions:use-latest-releases
    .\mvnw.cmd versions:commit
}

# Main script logic
switch (`$Command.ToLower()) {
    "build" { Build-Application }
    "test" { Test-Application }
    "clean" { Clean-Application }
    "run" { Run-Application }
    "run-docker" { Run-Docker }
    "deploy" { Deploy-Application }
    "stop" { Stop-Application }
    "logs" { Show-Logs }
    "status" { Show-Status }
    "check" { Check-CodeQuality }
    "coverage" { Generate-Coverage }
    "security" { Run-SecurityScan }
    "update-deps" { Update-Dependencies }
    default { Show-Help }
}
"@
    $commonTasks | Out-File -FilePath "scripts\common-tasks.ps1" -Encoding UTF8
}

# Create README for CI/CD
if (-not (Test-Path "README-CICD.md")) {
    Write-Log "Creating CI/CD README..."
    $readmeContent = @"
# BarberShop CI/CD Setup

This document provides quick setup instructions for the CI/CD pipeline.

## Quick Start

1. **Setup CI/CD Environment:**
   ```powershell
   .\scripts\setup-cicd.ps1
   ```

2. **Run Tests:**
   ```powershell
   .\scripts\common-tasks.ps1 test
   ```

3. **Build Application:**
   ```powershell
   .\scripts\common-tasks.ps1 build
   ```

4. **Run with Docker:**
   ```powershell
   .\scripts\common-tasks.ps1 run-docker
   ```

5. **Deploy:**
   ```powershell
   .\scripts\common-tasks.ps1 deploy
   ```

## Available Commands

Run `.\scripts\common-tasks.ps1` to see all available commands.

## GitHub Actions

The CI/CD pipeline includes:
- Automated testing
- Code quality checks
- Security scanning
- Docker image building
- Automated deployment
- Auto-commit functionality

## Environment Files

- `.env.template` - Template for environment variables
- `.env.dev` - Development environment
- `.env.prod` - Production environment template

## Docker Compose

- `docker-compose.yml` - Main configuration
- `docker-compose.override.yml` - Development overrides

## Scripts

- `scripts\deploy.bat` - Deployment script (Windows)
- `scripts\deploy.sh` - Deployment script (Linux/macOS)
- `scripts\setup-cicd.ps1` - CI/CD setup script (PowerShell)
- `scripts\common-tasks.ps1` - Common tasks script (PowerShell)

## Documentation

See `docs\CI_CD_GUIDE.md` for detailed documentation.
"@
    $readmeContent | Out-File -FilePath "README-CICD.md" -Encoding UTF8
}

# Test the setup
Write-Log "Testing the setup..."

# Test Maven wrapper
if (Test-Path "mvnw.cmd") {
    Write-Log "Testing Maven wrapper..."
    .\mvnw.cmd --version
}

# Test Docker
try {
    docker --version
    Write-Log "Docker test successful"
} catch {
    Write-Warning "Docker test failed"
}

# Test Docker Compose
try {
    docker-compose --version
    Write-Log "Docker Compose test successful"
} catch {
    Write-Warning "Docker Compose test failed"
}

# Final instructions
Write-Log "CI/CD setup completed successfully!"
Write-Host ""
Write-Info "Next steps:"
Write-Host "1. Configure GitHub repository secrets if using GitHub Actions"
Write-Host "2. Review and customize environment files (.env.*)"
Write-Host "3. Test the deployment: .\scripts\common-tasks.ps1 run-docker"
Write-Host "4. Run code quality checks: .\scripts\common-tasks.ps1 check"
Write-Host "5. See available commands: .\scripts\common-tasks.ps1"
Write-Host ""
Write-Info "For detailed documentation, see docs\CI_CD_GUIDE.md"
Write-Host ""
Write-Warning "Remember to:"
Write-Host "- Set up GitHub repository secrets for production deployment"
Write-Host "- Review and customize environment variables"
Write-Host "- Test the pipeline in a development environment first"
