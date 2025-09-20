#!/bin/bash

# BarberShop Deployment Script
# This script handles the deployment of the BarberShop application

set -e  # Exit on any error

# Configuration
APP_NAME="barbershop"
DOCKER_COMPOSE_FILE="docker-compose.yml"
BACKUP_DIR="backups"
LOG_FILE="deployment.log"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Logging function
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1" | tee -a $LOG_FILE
}

error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a $LOG_FILE
    exit 1
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1" | tee -a $LOG_FILE
}

# Check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        error "Docker is not running. Please start Docker and try again."
    fi
    log "Docker is running"
}

# Check if Docker Compose is available
check_docker_compose() {
    if ! command -v docker-compose &> /dev/null; then
        error "Docker Compose is not installed. Please install Docker Compose and try again."
    fi
    log "Docker Compose is available"
}

# Create backup directory
create_backup_dir() {
    if [ ! -d "$BACKUP_DIR" ]; then
        mkdir -p "$BACKUP_DIR"
        log "Created backup directory: $BACKUP_DIR"
    fi
}

# Backup current deployment
backup_current_deployment() {
    if [ -f "$DOCKER_COMPOSE_FILE" ]; then
        BACKUP_FILE="$BACKUP_DIR/backup-$(date +%Y%m%d-%H%M%S).yml"
        cp "$DOCKER_COMPOSE_FILE" "$BACKUP_FILE"
        log "Backed up current deployment to: $BACKUP_FILE"
    fi
}

# Pull latest images
pull_latest_images() {
    log "Pulling latest Docker images..."
    docker-compose pull
    log "Successfully pulled latest images"
}

# Build application
build_application() {
    log "Building application..."
    docker-compose build --no-cache
    log "Application built successfully"
}

# Run tests
run_tests() {
    log "Running tests..."
    if docker-compose run --rm barbershop-app ./mvnw test; then
        log "Tests passed successfully"
    else
        error "Tests failed. Deployment aborted."
    fi
}

# Stop current deployment
stop_current_deployment() {
    log "Stopping current deployment..."
    docker-compose down
    log "Current deployment stopped"
}

# Start new deployment
start_deployment() {
    log "Starting new deployment..."
    docker-compose up -d
    log "New deployment started"
}

# Wait for services to be healthy
wait_for_services() {
    log "Waiting for services to be healthy..."
    
    # Wait for MySQL
    log "Waiting for MySQL to be ready..."
    timeout=60
    while [ $timeout -gt 0 ]; do
        if docker-compose exec mysql mysqladmin ping -h localhost --silent; then
            log "MySQL is ready"
            break
        fi
        sleep 2
        timeout=$((timeout - 2))
    done
    
    if [ $timeout -le 0 ]; then
        error "MySQL failed to start within 60 seconds"
    fi
    
    # Wait for application
    log "Waiting for application to be ready..."
    timeout=120
    while [ $timeout -gt 0 ]; do
        if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
            log "Application is ready"
            break
        fi
        sleep 5
        timeout=$((timeout - 5))
    done
    
    if [ $timeout -le 0 ]; then
        error "Application failed to start within 120 seconds"
    fi
}

# Run health checks
run_health_checks() {
    log "Running health checks..."
    
    # Check if application is responding
    if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        log "Application health check passed"
    else
        error "Application health check failed"
    fi
    
    # Check if database is accessible
    if docker-compose exec mysql mysql -u barbershop -pbarbershop -e "SELECT 1" > /dev/null 2>&1; then
        log "Database health check passed"
    else
        error "Database health check failed"
    fi
}

# Clean up old images
cleanup_old_images() {
    log "Cleaning up old Docker images..."
    docker image prune -f
    log "Old images cleaned up"
}

# Main deployment function
deploy() {
    log "Starting deployment process..."
    
    check_docker
    check_docker_compose
    create_backup_dir
    backup_current_deployment
    
    # Pull latest changes if in a git repository
    if [ -d ".git" ]; then
        log "Pulling latest changes from git..."
        git pull origin main
    fi
    
    pull_latest_images
    build_application
    run_tests
    stop_current_deployment
    start_deployment
    wait_for_services
    run_health_checks
    cleanup_old_images
    
    log "Deployment completed successfully!"
    log "Application is available at: http://localhost:8080"
}

# Rollback function
rollback() {
    log "Starting rollback process..."
    
    # Find the latest backup
    LATEST_BACKUP=$(ls -t $BACKUP_DIR/backup-*.yml 2>/dev/null | head -n1)
    
    if [ -z "$LATEST_BACKUP" ]; then
        error "No backup files found for rollback"
    fi
    
    log "Rolling back to: $LATEST_BACKUP"
    
    stop_current_deployment
    cp "$LATEST_BACKUP" "$DOCKER_COMPOSE_FILE"
    start_deployment
    wait_for_services
    run_health_checks
    
    log "Rollback completed successfully!"
}

# Show usage
usage() {
    echo "Usage: $0 [deploy|rollback|status|logs]"
    echo ""
    echo "Commands:"
    echo "  deploy   - Deploy the application"
    echo "  rollback - Rollback to previous deployment"
    echo "  status   - Show deployment status"
    echo "  logs     - Show application logs"
    echo ""
}

# Show status
show_status() {
    log "Checking deployment status..."
    docker-compose ps
}

# Show logs
show_logs() {
    log "Showing application logs..."
    docker-compose logs -f
}

# Main script logic
case "${1:-deploy}" in
    deploy)
        deploy
        ;;
    rollback)
        rollback
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs
        ;;
    *)
        usage
        exit 1
        ;;
esac
