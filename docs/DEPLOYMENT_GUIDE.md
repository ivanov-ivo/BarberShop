# Deployment Guide - BarberShop

## 📋 Table of Contents
- [Prerequisites](#prerequisites)
- [Development Environment](#development-environment)
- [Production Deployment](#production-deployment)
- [Docker Deployment](#docker-deployment)
- [Cloud Deployment](#cloud-deployment)
- [Database Setup](#database-setup)
- [Security Configuration](#security-configuration)
- [Monitoring and Logging](#monitoring-and-logging)
- [Troubleshooting](#troubleshooting)

## 🔧 Prerequisites

### System Requirements
- **Java**: OpenJDK 17 or Oracle JDK 17
- **Database**: MySQL 8.0 or higher
- **Memory**: Minimum 2GB RAM (4GB recommended)
- **Storage**: 10GB available space
- **Network**: Internet access for dependencies

### Software Dependencies
- **Maven**: 3.6 or higher
- **Git**: For version control
- **MySQL Client**: For database management

## 🛠 Development Environment

### Local Development Setup

#### 1. Clone Repository
```bash
git clone <repository-url>
cd BarberShop
```

#### 2. Database Setup
```sql
-- Create database
CREATE DATABASE barbershop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user
CREATE USER 'barbershop_dev'@'localhost' IDENTIFIED BY 'dev_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON barbershop.* TO 'barbershop_dev'@'localhost';
FLUSH PRIVILEGES;
```

#### 3. Application Configuration
Create `src/main/resources/application-dev.properties`:
```properties
# Development Configuration
spring.profiles.active=dev
spring.datasource.url=jdbc:mysql://localhost:3306/barbershop
spring.datasource.username=barbershop_dev
spring.datasource.password=dev_password

# Development-specific settings
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
logging.level.com.example.barbershop=DEBUG

# File upload path
file.upload.path=./uploads/
```

#### 4. Build and Run
```bash
# Build project
mvn clean install

# Run with development profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

#### 5. Verify Installation
- Access application at `http://localhost:8080`
- Check database connectivity
- Verify file upload functionality

## 🚀 Production Deployment

### Traditional Server Deployment

#### 1. Server Preparation
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java
sudo apt install openjdk-17-jdk -y

# Install MySQL
sudo apt install mysql-server -y

# Install Nginx
sudo apt install nginx -y
```

#### 2. Database Setup
```sql
-- Create production database
CREATE DATABASE barbershop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create production user
CREATE USER 'barbershop_prod'@'localhost' IDENTIFIED BY 'strong_production_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON barbershop.* TO 'barbershop_prod'@'localhost';
FLUSH PRIVILEGES;
```

#### 3. Application Configuration
Create `src/main/resources/application-prod.properties`:
```properties
# Production Configuration
spring.profiles.active=prod
spring.datasource.url=jdbc:mysql://localhost:3306/barbershop
spring.datasource.username=barbershop_prod
spring.datasource.password=strong_production_password

# Production settings
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=validate
logging.level.com.example.barbershop=INFO

# File upload configuration
file.upload.path=/var/barbershop/uploads/
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Server configuration
server.port=8080
server.tomcat.max-threads=200
server.tomcat.min-spare-threads=10

# Security configuration
spring.security.csrf.enabled=true
```

#### 4. Build Production JAR
```bash
# Build with production profile
mvn clean package -Pprod -DskipTests

# JAR file will be in target/BarberShop-0.0.1-SNAPSHOT.jar
```

#### 5. Create Systemd Service
Create `/etc/systemd/system/barbershop.service`:
```ini
[Unit]
Description=BarberShop Application
After=network.target mysql.service

[Service]
Type=simple
User=barbershop
Group=barbershop
WorkingDirectory=/opt/barbershop
ExecStart=/usr/bin/java -Xms512m -Xmx1024m -jar BarberShop-0.0.1-SNAPSHOT.jar
ExecStop=/bin/kill -TERM $MAINPID
Restart=always
RestartSec=10
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="JAVA_OPTS=-Djava.security.egd=file:/dev/./urandom"

[Install]
WantedBy=multi-user.target
```

#### 6. Setup Application Directory
```bash
# Create application user
sudo useradd -r -s /bin/false barbershop

# Create application directory
sudo mkdir -p /opt/barbershop
sudo mkdir -p /var/barbershop/uploads

# Copy application files
sudo cp target/BarberShop-0.0.1-SNAPSHOT.jar /opt/barbershop/
sudo chown -R barbershop:barbershop /opt/barbershop
sudo chown -R barbershop:barbershop /var/barbershop

# Set permissions
sudo chmod 755 /opt/barbershop
sudo chmod 644 /opt/barbershop/BarberShop-0.0.1-SNAPSHOT.jar
```

#### 7. Configure Nginx
Create `/etc/nginx/sites-available/barbershop`:
```nginx
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com www.your-domain.com;

    # SSL Configuration
    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;

    # Security headers
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    # Proxy to Spring Boot application
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }

    # Static file serving
    location /uploads/ {
        alias /var/barbershop/uploads/;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;
}
```

#### 8. Enable and Start Services
```bash
# Enable Nginx site
sudo ln -s /etc/nginx/sites-available/barbershop /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx

# Start application service
sudo systemctl daemon-reload
sudo systemctl enable barbershop
sudo systemctl start barbershop

# Check status
sudo systemctl status barbershop
```

## 🐳 Docker Deployment

### Dockerfile
Create `Dockerfile`:
```dockerfile
# Use OpenJDK 17 slim image
FROM openjdk:17-jre-slim

# Set working directory
WORKDIR /app

# Create application user
RUN groupadd -r barbershop && useradd -r -g barbershop barbershop

# Copy application JAR
COPY target/BarberShop-0.0.1-SNAPSHOT.jar app.jar

# Create uploads directory
RUN mkdir -p /app/uploads && chown -R barbershop:barbershop /app

# Switch to application user
USER barbershop

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Start application
ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", "-jar", "app.jar"]
```

### Docker Compose
Create `docker-compose.yml`:
```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/barbershop
      - SPRING_DATASOURCE_USERNAME=barbershop
      - SPRING_DATASOURCE_PASSWORD=barbershop_password
    volumes:
      - uploads:/app/uploads
    depends_on:
      - db
    restart: unless-stopped

  db:
    image: mysql:8.0
    environment:
      - MYSQL_DATABASE=barbershop
      - MYSQL_USER=barbershop
      - MYSQL_PASSWORD=barbershop_password
      - MYSQL_ROOT_PASSWORD=root_password
    volumes:
      - mysql_data:/var/lib/mysql
      - ./db/init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "3306:3306"
    restart: unless-stopped

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
    depends_on:
      - app
    restart: unless-stopped

volumes:
  mysql_data:
  uploads:
```

### Docker Deployment Commands
```bash
# Build and start services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Update application
docker-compose build app
docker-compose up -d app
```

## ☁️ Cloud Deployment

### AWS Deployment

#### 1. EC2 Instance Setup
```bash
# Launch EC2 instance (t3.medium recommended)
# Install Java and MySQL
sudo yum update -y
sudo yum install java-17-amazon-corretto -y
sudo yum install mysql -y
```

#### 2. RDS Database Setup
```sql
-- Create RDS MySQL instance
-- Endpoint: barbershop-db.region.rds.amazonaws.com
-- Database: barbershop
-- Username: barbershop_user
-- Password: strong_password
```

#### 3. Application Configuration
```properties
# AWS Configuration
spring.datasource.url=jdbc:mysql://barbershop-db.region.rds.amazonaws.com:3306/barbershop
spring.datasource.username=barbershop_user
spring.datasource.password=strong_password

# S3 for file storage (optional)
aws.s3.bucket=barbershop-uploads
aws.s3.region=us-east-1
```

#### 4. Load Balancer Setup
- Create Application Load Balancer
- Configure target group
- Set up SSL certificate
- Configure health checks

### Google Cloud Platform

#### 1. App Engine Deployment
Create `app.yaml`:
```yaml
runtime: java17
env: standard

instance_class: F1

automatic_scaling:
  target_cpu_utilization: 0.6
  min_instances: 1
  max_instances: 10

env_variables:
  SPRING_PROFILES_ACTIVE: "gcp"
  SPRING_DATASOURCE_URL: "jdbc:mysql:///barbershop?cloudSqlInstance=project:region:instance&socketFactory=com.google.cloud.sql.mysql.SocketFactory"
```

#### 2. Cloud SQL Setup
```bash
# Create Cloud SQL instance
gcloud sql instances create barbershop-db \
  --database-version=MYSQL_8_0 \
  --tier=db-f1-micro \
  --region=us-central1

# Create database
gcloud sql databases create barbershop --instance=barbershop-db

# Create user
gcloud sql users create barbershop-user \
  --instance=barbershop-db \
  --password=strong_password
```

#### 3. Deploy to App Engine
```bash
# Deploy application
gcloud app deploy

# View application
gcloud app browse
```

## 🗄 Database Setup

### Production Database Configuration

#### 1. MySQL Configuration
Edit `/etc/mysql/mysql.conf.d/mysqld.cnf`:
```ini
[mysqld]
# Performance settings
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 2
innodb_flush_method = O_DIRECT

# Connection settings
max_connections = 200
max_connect_errors = 1000000

# Query cache
query_cache_type = 1
query_cache_size = 64M
query_cache_limit = 2M

# Character set
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
```

#### 2. Database Backup Strategy
```bash
#!/bin/bash
# backup.sh
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/var/backups/barbershop"
mkdir -p $BACKUP_DIR

# Create backup
mysqldump -u barbershop_prod -p barbershop > $BACKUP_DIR/barbershop_$DATE.sql

# Compress backup
gzip $BACKUP_DIR/barbershop_$DATE.sql

# Keep only last 7 days
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete
```

#### 3. Automated Backup Cron Job
```bash
# Add to crontab
0 2 * * * /opt/barbershop/backup.sh
```

## 🔒 Security Configuration

### SSL/TLS Setup

#### 1. Let's Encrypt Certificate
```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y

# Obtain certificate
sudo certbot --nginx -d your-domain.com -d www.your-domain.com

# Auto-renewal
sudo crontab -e
# Add: 0 12 * * * /usr/bin/certbot renew --quiet
```

#### 2. Security Headers
Add to Nginx configuration:
```nginx
# Security headers
add_header X-Frame-Options DENY;
add_header X-Content-Type-Options nosniff;
add_header X-XSS-Protection "1; mode=block";
add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
add_header Content-Security-Policy "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';";
```

### Firewall Configuration
```bash
# Configure UFW
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw deny 3306/tcp
sudo ufw enable
```

## 📊 Monitoring and Logging

### Application Monitoring

#### 1. Spring Boot Actuator
Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Configure in `application.properties`:
```properties
# Actuator configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
management.metrics.export.prometheus.enabled=true
```

#### 2. Logging Configuration
```properties
# Logging configuration
logging.level.com.example.barbershop=INFO
logging.level.org.springframework.security=WARN
logging.file.name=/var/log/barbershop/application.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.logback.rollingpolicy.max-file-size=10MB
logging.logback.rollingpolicy.max-history=30
```

#### 3. Log Rotation
Create `/etc/logrotate.d/barbershop`:
```
/var/log/barbershop/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 barbershop barbershop
    postrotate
        systemctl reload barbershop
    endscript
}
```

### Health Checks
```bash
# Application health
curl http://localhost:8080/actuator/health

# Database connectivity
mysql -u barbershop_prod -p -e "SELECT 1;"

# File system space
df -h /var/barbershop/uploads
```

## 🔧 Troubleshooting

### Common Issues

#### 1. Application Won't Start
```bash
# Check Java version
java -version

# Check port availability
netstat -tulpn | grep 8080

# Check logs
sudo journalctl -u barbershop -f
```

#### 2. Database Connection Issues
```bash
# Test database connectivity
mysql -u barbershop_prod -p -h localhost barbershop

# Check MySQL status
sudo systemctl status mysql

# Check MySQL logs
sudo tail -f /var/log/mysql/error.log
```

#### 3. File Upload Issues
```bash
# Check directory permissions
ls -la /var/barbershop/uploads

# Check disk space
df -h

# Check file ownership
sudo chown -R barbershop:barbershop /var/barbershop/uploads
```

#### 4. Performance Issues
```bash
# Check memory usage
free -h

# Check CPU usage
top

# Check disk I/O
iotop

# Check application threads
jstack <pid>
```

### Debug Mode
Enable debug logging:
```properties
logging.level.com.example.barbershop=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### Performance Tuning
```properties
# JVM tuning
JAVA_OPTS="-Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Database connection pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

---

This deployment guide provides comprehensive instructions for deploying the BarberShop application in various environments. Follow the appropriate section based on your deployment requirements and infrastructure. 