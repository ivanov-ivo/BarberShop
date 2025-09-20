@echo off
setlocal enabledelayedexpansion

REM BarberShop Deployment Script for Windows
REM This script handles the deployment of the BarberShop application

set APP_NAME=barbershop
set DOCKER_COMPOSE_FILE=docker-compose.yml
set BACKUP_DIR=backups
set LOG_FILE=deployment.log

REM Create backup directory if it doesn't exist
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

REM Logging function
:log
echo [%date% %time%] %~1 | tee -a %LOG_FILE%
goto :eof

:error
echo [ERROR] %~1 | tee -a %LOG_FILE%
exit /b 1

:warning
echo [WARNING] %~1 | tee -a %LOG_FILE%
goto :eof

REM Check if Docker is running
:check_docker
docker info >nul 2>&1
if errorlevel 1 (
    call :error "Docker is not running. Please start Docker and try again."
)
call :log "Docker is running"
goto :eof

REM Check if Docker Compose is available
:check_docker_compose
docker-compose --version >nul 2>&1
if errorlevel 1 (
    call :error "Docker Compose is not installed. Please install Docker Compose and try again."
)
call :log "Docker Compose is available"
goto :eof

REM Backup current deployment
:backup_current_deployment
if exist "%DOCKER_COMPOSE_FILE%" (
    set BACKUP_FILE=%BACKUP_DIR%\backup-%date:~-4,4%%date:~-10,2%%date:~-7,2%-%time:~0,2%%time:~3,2%%time:~6,2%.yml
    set BACKUP_FILE=!BACKUP_FILE: =0!
    copy "%DOCKER_COMPOSE_FILE%" "!BACKUP_FILE!" >nul
    call :log "Backed up current deployment to: !BACKUP_FILE!"
)
goto :eof

REM Pull latest images
:pull_latest_images
call :log "Pulling latest Docker images..."
docker-compose pull
if errorlevel 1 (
    call :error "Failed to pull latest images"
)
call :log "Successfully pulled latest images"
goto :eof

REM Build application
:build_application
call :log "Building application..."
docker-compose build --no-cache
if errorlevel 1 (
    call :error "Failed to build application"
)
call :log "Application built successfully"
goto :eof

REM Run tests
:run_tests
call :log "Running tests..."
docker-compose run --rm barbershop-app ./mvnw test
if errorlevel 1 (
    call :error "Tests failed. Deployment aborted."
)
call :log "Tests passed successfully"
goto :eof

REM Stop current deployment
:stop_current_deployment
call :log "Stopping current deployment..."
docker-compose down
call :log "Current deployment stopped"
goto :eof

REM Start new deployment
:start_deployment
call :log "Starting new deployment..."
docker-compose up -d
if errorlevel 1 (
    call :error "Failed to start deployment"
)
call :log "New deployment started"
goto :eof

REM Wait for services to be healthy
:wait_for_services
call :log "Waiting for services to be healthy..."

REM Wait for MySQL
call :log "Waiting for MySQL to be ready..."
set timeout=60
:wait_mysql
docker-compose exec mysql mysqladmin ping -h localhost --silent >nul 2>&1
if not errorlevel 1 (
    call :log "MySQL is ready"
    goto :wait_app
)
timeout /t 2 /nobreak >nul
set /a timeout-=2
if !timeout! leq 0 (
    call :error "MySQL failed to start within 60 seconds"
)
goto :wait_mysql

REM Wait for application
:wait_app
call :log "Waiting for application to be ready..."
set timeout=120
:wait_app_loop
curl -f http://localhost:8080/actuator/health >nul 2>&1
if not errorlevel 1 (
    call :log "Application is ready"
    goto :health_checks
)
timeout /t 5 /nobreak >nul
set /a timeout-=5
if !timeout! leq 0 (
    call :error "Application failed to start within 120 seconds"
)
goto :wait_app_loop

REM Run health checks
:health_checks
call :log "Running health checks..."

REM Check if application is responding
curl -f http://localhost:8080/actuator/health >nul 2>&1
if errorlevel 1 (
    call :error "Application health check failed"
)
call :log "Application health check passed"

REM Check if database is accessible
docker-compose exec mysql mysql -u barbershop -pbarbershop -e "SELECT 1" >nul 2>&1
if errorlevel 1 (
    call :error "Database health check failed"
)
call :log "Database health check passed"
goto :eof

REM Clean up old images
:cleanup_old_images
call :log "Cleaning up old Docker images..."
docker image prune -f
call :log "Old images cleaned up"
goto :eof

REM Main deployment function
:deploy
call :log "Starting deployment process..."

call :check_docker
call :check_docker_compose
call :backup_current_deployment

REM Pull latest changes if in a git repository
if exist ".git" (
    call :log "Pulling latest changes from git..."
    git pull origin main
)

call :pull_latest_images
call :build_application
call :run_tests
call :stop_current_deployment
call :start_deployment
call :wait_for_services
call :health_checks
call :cleanup_old_images

call :log "Deployment completed successfully!"
call :log "Application is available at: http://localhost:8080"
goto :eof

REM Show status
:show_status
call :log "Checking deployment status..."
docker-compose ps
goto :eof

REM Show logs
:show_logs
call :log "Showing application logs..."
docker-compose logs -f
goto :eof

REM Main script logic
if "%1"=="deploy" goto :deploy
if "%1"=="status" goto :show_status
if "%1"=="logs" goto :show_logs
if "%1"=="" goto :deploy

echo Usage: %0 [deploy^|status^|logs]
echo.
echo Commands:
echo   deploy   - Deploy the application
echo   status   - Show deployment status
echo   logs     - Show application logs
echo.
exit /b 1
