# Docker Setup Guide for BarberShop Application

This guide explains how to run the BarberShop application using Docker and Docker Compose.

## Prerequisites

- Docker Desktop (Windows/Mac) or Docker Engine (Linux)
- Docker Compose (usually included with Docker Desktop)

## Quick Start

1. **Clone the repository** (if not already done):
   ```bash
   git clone <repository-url>
   cd BarberShop
   ```

2. **Build and run the application**:
   ```bash
   docker-compose up --build
   ```

3. **Access the application**:
   - Application: http://localhost:8080
   - MySQL Database: localhost:3306

## Docker Configuration Files

### Dockerfile
- Uses OpenJDK 17 as base image
- Multi-stage build for optimal image size
- Copies Maven wrapper and dependencies first for better caching
- Creates uploads directory for file storage
- Exposes port 8080

### docker-compose.yml
- **MySQL Service**: 
  - Uses MySQL 8.0
  - Database: `barbershop`
  - User: `barbershop` / Password: `barbershop`
  - Port: 3306
  - Includes health checks
  - Automatically runs `sqlScript.sql` on initialization

- **BarberShop App Service**:
  - Builds from local Dockerfile
  - Port: 8080
  - Depends on MySQL service
  - Includes health checks
  - Volume for file uploads

### .dockerignore
- Excludes unnecessary files from Docker build context
- Improves build performance and reduces image size

## Environment Configuration

### docker.env
Contains environment variables for Docker containers:
- Database credentials
- Spring Boot configuration
- JPA settings

### application-docker.properties
Spring Boot configuration file for Docker environment with:
- Database connection settings
- File upload limits
- Logging configuration

## Available Commands

### Build and Run
```bash
# Build and start all services
docker-compose up --build

# Run in detached mode
docker-compose up -d --build

# Start only specific services
docker-compose up mysql
docker-compose up barbershop-app
```

### Stop Services
```bash
# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: This will delete database data)
docker-compose down -v
```

### View Logs
```bash
# View all logs
docker-compose logs

# View logs for specific service
docker-compose logs barbershop-app
docker-compose logs mysql

# Follow logs in real-time
docker-compose logs -f barbershop-app
```

### Database Management
```bash
# Access MySQL container
docker-compose exec mysql mysql -u barbershop -p barbershop

# Backup database
docker-compose exec mysql mysqldump -u barbershop -p barbershop > backup.sql

# Restore database
docker-compose exec -T mysql mysql -u barbershop -p barbershop < backup.sql
```

### Development Commands
```bash
# Rebuild only the application (without MySQL)
docker-compose build barbershop-app

# Execute commands in running container
docker-compose exec barbershop-app bash

# View container status
docker-compose ps
```

## Default Credentials

### Application Login
- **Admin**: admin@gmail.com / admin123
- **Barber**: barberDatabaseEntity@gmail.com / barber123

### Database
- **Host**: localhost:3306
- **Database**: barbershop
- **Username**: barbershop
- **Password**: barbershop
- **Root Password**: rootpassword

## Troubleshooting

### Common Issues

1. **Port Already in Use**:
   ```bash
   # Check what's using the port
   netstat -tulpn | grep :8080
   # Or change ports in docker-compose.yml
   ```

2. **Database Connection Issues**:
   ```bash
   # Check MySQL container status
   docker-compose ps mysql
   # View MySQL logs
   docker-compose logs mysql
   ```

3. **Application Won't Start**:
   ```bash
   # Check application logs
   docker-compose logs barbershop-app
   # Rebuild the application
   docker-compose build --no-cache barbershop-app
   ```

4. **File Upload Issues**:
   - Ensure uploads volume is properly mounted
   - Check file size limits in application properties

### Health Checks
Both services include health checks:
- MySQL: Uses `mysqladmin ping`
- Application: Uses HTTP health endpoint (requires Spring Boot Actuator)

### Volumes
- `mysql_data`: Persistent storage for MySQL data
- `uploads`: Storage for uploaded files (barber photos, etc.)

## Production Considerations

For production deployment, consider:

1. **Security**:
   - Change default passwords
   - Use Docker secrets for sensitive data
   - Enable SSL/TLS

2. **Performance**:
   - Use production-grade MySQL configuration
   - Configure JVM memory settings
   - Use reverse proxy (nginx)

3. **Monitoring**:
   - Add monitoring tools (Prometheus, Grafana)
   - Configure log aggregation
   - Set up alerting

4. **Backup**:
   - Regular database backups
   - Volume backups
   - Application configuration backups

## Development vs Production

### Development
- Uses `application-docker.properties`
- Debug logging enabled
- Hot reload not available (rebuild required)

### Production
- Create `application-prod.properties`
- Disable debug logging
- Use external database if needed
- Configure proper security settings

## Support

For issues or questions:
1. Check the logs: `docker-compose logs`
2. Verify container status: `docker-compose ps`
3. Check Docker Desktop/system resources
4. Review this documentation
