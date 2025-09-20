# CI/CD Implementation Guide

This document describes the Continuous Integration and Continuous Deployment (CI/CD) implementation for the BarberShop application.

## Overview

The CI/CD pipeline is implemented using GitHub Actions and includes the following features:

- **Automated Testing**: Unit tests, integration tests, and code quality checks
- **Automated Building**: Maven build with Docker image creation
- **Automated Deployment**: Docker-based deployment to production
- **Automated Git Operations**: Auto-commit and push changes
- **Security Scanning**: OWASP dependency check and vulnerability scanning
- **Code Quality**: SpotBugs, Checkstyle, and JaCoCo code coverage

## Workflow Files

### 1. Main CI/CD Pipeline (`.github/workflows/ci-cd.yml`)

This is the primary workflow that runs on every push and pull request:

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` branch
- Manual workflow dispatch

**Jobs:**
1. **Test**: Runs unit tests with MySQL service
2. **Build**: Builds the application with Maven
3. **Docker Build**: Creates and pushes Docker images
4. **Deploy**: Deploys to production (main branch only)
5. **Auto Commit**: Automatically commits and pushes changes
6. **Security Scan**: Runs OWASP dependency check
7. **Code Quality**: Runs SpotBugs and Checkstyle

### 2. Auto Update Workflow (`.github/workflows/auto-update.yml`)

Automatically updates dependencies and generates changelogs:

**Triggers:**
- Daily at 2 AM UTC (cron schedule)
- Manual workflow dispatch

**Features:**
- Updates Maven dependencies to latest versions
- Generates changelog for recent changes
- Auto-commits updates with `[skip ci]` to prevent loops

### 3. Release Workflow (`.github/workflows/release.yml`)

Handles version releases and creates GitHub releases:

**Triggers:**
- Push of version tags (e.g., `v1.0.0`)
- Manual workflow dispatch with version input

**Features:**
- Creates GitHub releases with release notes
- Uploads JAR artifacts
- Builds and pushes Docker images with version tags

## Configuration Files

### Maven Configuration (`pom.xml`)

Enhanced with the following plugins:

- **Maven Surefire Plugin**: Unit test execution
- **Maven Failsafe Plugin**: Integration test execution
- **SpotBugs Plugin**: Static code analysis
- **Checkstyle Plugin**: Code style enforcement
- **JaCoCo Plugin**: Code coverage reporting
- **Versions Plugin**: Dependency management

### Checkstyle Configuration (`checkstyle.xml`)

Comprehensive code style rules including:
- Javadoc requirements
- Naming conventions
- Import organization
- Code formatting
- Block structure
- Design patterns

## Deployment Scripts

### Linux/macOS (`scripts/deploy.sh`)

Features:
- Docker and Docker Compose validation
- Backup creation before deployment
- Health checks for services
- Rollback capability
- Comprehensive logging

**Usage:**
```bash
./scripts/deploy.sh deploy    # Deploy application
./scripts/deploy.sh rollback  # Rollback to previous version
./scripts/deploy.sh status    # Show deployment status
./scripts/deploy.sh logs      # Show application logs
```

### Windows (`scripts/deploy.bat`)

Windows batch file equivalent with the same functionality.

**Usage:**
```cmd
scripts\deploy.bat deploy    # Deploy application
scripts\deploy.bat status    # Show deployment status
scripts\deploy.bat logs      # Show application logs
```

## Environment Variables

### Required GitHub Secrets

Configure these in your GitHub repository settings:

- `GITHUB_TOKEN`: Automatically provided by GitHub Actions
- `DOCKER_REGISTRY`: Container registry URL (default: `ghcr.io`)
- `IMAGE_NAME`: Docker image name (default: repository name)

### Application Environment Variables

- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_PROFILES_ACTIVE`: Active Spring profile

## Automated Git Operations

The CI/CD pipeline includes automated git operations that:

1. **Auto-commit Changes**: Automatically commits generated files, configuration updates, and build artifacts
2. **Skip CI**: Uses `[skip ci]` in commit messages to prevent infinite loops
3. **Dependency Updates**: Automatically updates Maven dependencies to latest versions
4. **Changelog Generation**: Creates and updates changelog files

### Commit Message Format

- `Auto-commit: Update generated files and configurations [skip ci]`
- `Auto-update: Update dependencies [skip ci]`
- `Auto-update: Update changelog [skip ci]`

## Testing Strategy

### Unit Tests
- Located in `src/test/java/`
- Run with Maven Surefire Plugin
- Include controller, service, and repository tests
- Use Mockito for mocking dependencies

### Integration Tests
- Located in `src/test/java/` with `*IntegrationTest.java` naming
- Run with Maven Failsafe Plugin
- Use `@SpringBootTest` for full application context

### Test Database
- Uses MySQL 8.0 service in GitHub Actions
- Separate test configuration
- Automatic cleanup after tests

## Code Quality Gates

### SpotBugs
- Static analysis for potential bugs
- Configured with maximum effort and low threshold
- XML output for reporting

### Checkstyle
- Code style enforcement
- Comprehensive rule set
- Fails build on violations

### JaCoCo
- Code coverage reporting
- Minimum coverage requirements
- HTML and XML reports

## Security Scanning

### OWASP Dependency Check
- Scans for known vulnerabilities
- Fails on CVSS score ≥ 7
- HTML report generation
- Includes experimental and retired vulnerabilities

## Docker Configuration

### Multi-stage Build
- Optimized for layer caching
- Separate dependency download step
- Minimal final image size

### Health Checks
- Application health endpoint
- Database connectivity check
- Automatic restart on failure

### Environment Configuration
- Docker-specific properties
- Environment variable substitution
- Production-ready defaults

## Monitoring and Logging

### Application Logs
- Structured logging with timestamps
- Different log levels for different components
- Docker log aggregation

### Health Endpoints
- Spring Boot Actuator health endpoint
- Custom health indicators
- Prometheus metrics (if configured)

## Troubleshooting

### Common Issues

1. **Tests Failing**
   - Check database connectivity
   - Verify test data setup
   - Review test isolation

2. **Docker Build Failures**
   - Check Dockerfile syntax
   - Verify base image availability
   - Review build context

3. **Deployment Issues**
   - Check service health
   - Verify environment variables
   - Review container logs

### Debug Commands

```bash
# Check Docker status
docker-compose ps

# View application logs
docker-compose logs -f barbershop-app

# Check database connectivity
docker-compose exec mysql mysql -u barbershop -pbarbershop -e "SELECT 1"

# Test application health
curl -f http://localhost:8080/actuator/health
```

## Best Practices

1. **Branch Strategy**
   - Use feature branches for development
   - Merge to `develop` for testing
   - Merge to `main` for production

2. **Commit Messages**
   - Use conventional commit format
   - Include `[skip ci]` for automated commits
   - Be descriptive and clear

3. **Testing**
   - Write tests for all new features
   - Maintain high code coverage
   - Test edge cases and error conditions

4. **Security**
   - Regularly update dependencies
   - Review security scan results
   - Use least privilege principles

5. **Monitoring**
   - Monitor application health
   - Set up alerts for failures
   - Review logs regularly

## Future Enhancements

1. **Kubernetes Deployment**
   - Helm charts for deployment
   - Horizontal pod autoscaling
   - Service mesh integration

2. **Advanced Monitoring**
   - Prometheus metrics
   - Grafana dashboards
   - Distributed tracing

3. **Multi-environment Support**
   - Staging environment
   - Blue-green deployments
   - Canary releases

4. **Advanced Security**
   - Container image scanning
   - Runtime security monitoring
   - Secrets management
