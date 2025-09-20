#!/bin/bash

# BarberShop Docker Management Scripts

case "$1" in
    "start")
        echo "Starting BarberShop application..."
        docker-compose up --build -d
        echo "Application started! Access at http://localhost:8080"
        ;;
    "stop")
        echo "Stopping BarberShop application..."
        docker-compose down
        echo "Application stopped!"
        ;;
    "restart")
        echo "Restarting BarberShop application..."
        docker-compose down
        docker-compose up --build -d
        echo "Application restarted! Access at http://localhost:8080"
        ;;
    "logs")
        echo "Showing application logs..."
        docker-compose logs -f barbershop-app
        ;;
    "db-logs")
        echo "Showing database logs..."
        docker-compose logs -f mysql
        ;;
    "db-access")
        echo "Accessing MySQL database..."
        docker-compose exec mysql mysql -u barbershop -p barbershop
        ;;
    "clean")
        echo "Cleaning up Docker resources..."
        docker-compose down -v
        docker system prune -f
        echo "Cleanup completed!"
        ;;
    "status")
        echo "Checking service status..."
        docker-compose ps
        ;;
    "build")
        echo "Building application..."
        docker-compose build --no-cache
        echo "Build completed!"
        ;;
    *)
        echo "BarberShop Docker Management Script"
        echo "Usage: $0 {start|stop|restart|logs|db-logs|db-access|clean|status|build}"
        echo ""
        echo "Commands:"
        echo "  start     - Start the application"
        echo "  stop      - Stop the application"
        echo "  restart   - Restart the application"
        echo "  logs      - Show application logs"
        echo "  db-logs   - Show database logs"
        echo "  db-access - Access MySQL database"
        echo "  clean     - Clean up Docker resources"
        echo "  status    - Show service status"
        echo "  build     - Build the application"
        ;;
esac
