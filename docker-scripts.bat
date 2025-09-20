@echo off
REM BarberShop Docker Management Scripts for Windows

if "%1"=="start" (
    echo Starting BarberShop application...
    docker-compose up --build -d
    echo Application started! Access at http://localhost:8080
    goto :eof
)

if "%1"=="stop" (
    echo Stopping BarberShop application...
    docker-compose down
    echo Application stopped!
    goto :eof
)

if "%1"=="restart" (
    echo Restarting BarberShop application...
    docker-compose down
    docker-compose up --build -d
    echo Application restarted! Access at http://localhost:8080
    goto :eof
)

if "%1"=="logs" (
    echo Showing application logs...
    docker-compose logs -f barbershop-app
    goto :eof
)

if "%1"=="db-logs" (
    echo Showing database logs...
    docker-compose logs -f mysql
    goto :eof
)

if "%1"=="db-access" (
    echo Accessing MySQL database...
    docker-compose exec mysql mysql -u barbershop -p barbershop
    goto :eof
)

if "%1"=="clean" (
    echo Cleaning up Docker resources...
    docker-compose down -v
    docker system prune -f
    echo Cleanup completed!
    goto :eof
)

if "%1"=="status" (
    echo Checking service status...
    docker-compose ps
    goto :eof
)

if "%1"=="build" (
    echo Building application...
    docker-compose build --no-cache
    echo Build completed!
    goto :eof
)

echo BarberShop Docker Management Script
echo Usage: %0 {start^|stop^|restart^|logs^|db-logs^|db-access^|clean^|status^|build}
echo.
echo Commands:
echo   start     - Start the application
echo   stop      - Stop the application
echo   restart   - Restart the application
echo   logs      - Show application logs
echo   db-logs   - Show database logs
echo   db-access - Access MySQL database
echo   clean     - Clean up Docker resources
echo   status    - Show service status
echo   build     - Build the application
