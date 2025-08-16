# Technical Documentation - BarberShop

## 🏗 Architecture Overview

The BarberShop application follows a layered architecture pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │ Controllers │  │   Security  │  │    Thymeleaf        │ │
│  │             │  │   Config    │  │    Templates        │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Business Logic Layer                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │   Services  │  │ Validation  │  │   Schedulers        │ │
│  │             │  │             │  │                     │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Data Access Layer                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │ Repositories│  │   Entities  │  │   JPA/Hibernate     │ │
│  │             │  │             │  │                     │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Database Layer                           │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                    MySQL Database                       │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## 📦 Package Structure

### Controllers Package (`com.example.barbershop.controller`)

#### IndexController
- **Purpose**: Handles public-facing pages
- **Key Methods**:
  - `index()`: Displays homepage with barberDatabaseEntity listings
- **Dependencies**: BarberService, branches configuration

#### LoginController
- **Purpose**: Manages authentication flow
- **Key Methods**:
  - `showLoginForm()`: Displays login page
- **Security**: Integrates with Spring Security

#### DashboardController
- **Purpose**: Barber dashboard functionality
- **Key Methods**:
  - `dashboard()`: Shows barberDatabaseEntity's appointmentDatabaseEntities
- **Security**: Requires ROLE_BARBER or ROLE_ADMIN

#### AdminController
- **Purpose**: Administrative operations
- **Key Methods**:
  - `admin()`: Admin panel view
  - `addBarber()`: Create new barberDatabaseEntity
  - `editBarber()`: Update barberDatabaseEntity information
  - `deleteBarber()`: Remove barberDatabaseEntity
- **Security**: Requires ROLE_ADMIN

#### AppointmentsController
- **Purpose**: Appointment management
- **Key Methods**:
  - `booking()`: Create new appointmentDatabaseEntity
  - `editAppointment()`: Update appointmentDatabaseEntity
  - `deleteAppointment()`: Remove appointmentDatabaseEntity

### Services Package (`com.example.barbershop.service`)

#### UserService
```java
@Service
public class UserService {
    // UserDatabaseEntity CRUD operations
    // Password encoding
    // Role management
}
```

#### BarberService
```java
@Service
public class BarberService {
    // BarberDatabaseEntity CRUD operations
    // Branch filtering
    // Photo management
}
```

#### AppointmentService
```java
@Service
public class AppointmentService {
    // AppointmentDatabaseEntity CRUD operations
    // Date/time validation
    // Conflict checking
}
```

#### FileUploadService
```java
@Service
public class FileUploadService {
    // File upload handling
    // Image processing
    // Storage management
}
```

#### DeleteOldAppointmentsScheduler
```java
@Component
@EnableScheduling
public class DeleteOldAppointmentsScheduler {
    // Automated cleanup of old appointmentDatabaseEntities
    // Scheduled task execution
}
```

### Entities Package (`com.example.barbershop.entity`)

#### User Entity
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    private String username;
    private String password;
    private boolean enabled;
    private Long barberId;
    private String role;
}
```

#### Barber Entity
```java
@Entity
@Table(name = "barberDatabaseEntities")
public class Barber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String photo;
    private String branch;
    private String information;
}
```

#### Appointment Entity
```java
@Entity
@Table(name = "appointmentDatabaseEntities")
@IdClass(BarberId.class)
public class Appointment {
    @Id
    private Timestamp date;
    @Id
    private Long barberId;
    private String name;
    private String phone;
    private String comment;
}
```

### Repositories Package (`com.example.barbershop.dao`)

#### UserRepository
```java
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // Custom queries for userDatabaseEntity management
}
```

#### BarberRepository
```java
@Repository
public interface BarberRepository extends JpaRepository<Barber, Long> {
    // Custom queries for barberDatabaseEntity operations
}
```

#### AppointmentRepository
```java
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, BarberId> {
    // Custom queries for appointmentDatabaseEntity management
}
```

## 🔒 Security Implementation

### Security Configuration
```java
@Configuration
public class SecurityConfig {
    // URL-based security rules
    // Authentication configuration
    // Password encoding
    // Session management
}
```

### Authentication Flow
1. **Login Request**: User submits credentials
2. **Authentication**: Spring Security validates against database
3. **Role Assignment**: User roles are loaded from database
4. **Session Creation**: JSESSIONID cookie is set
5. **Redirect**: User is redirected based on role

### Authorization Rules
- `/`: Public access
- `/login`: Public access
- `/dashboard`: ROLE_BARBER, ROLE_ADMIN
- `/admin`: ROLE_ADMIN only
- `/appointmentDatabaseEntities/booking`: Public access
- Static resources: Public access

## 🗄 Database Design

### Entity Relationships
```
Users (1) ←→ (1) Barbers
Barbers (1) ←→ (N) Appointments
```

### Table Schemas

#### Users Table
```sql
CREATE TABLE users (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    id BIGINT,
    role VARCHAR(50)
);
```

#### Barbers Table
```sql
CREATE TABLE barberDatabaseEntities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    photo VARCHAR(500),
    branch VARCHAR(100),
    information TEXT
);
```

#### Appointments Table
```sql
CREATE TABLE appointmentDatabaseEntities (
    date TIMESTAMP,
    id BIGINT,
    name VARCHAR(255),
    phone VARCHAR(50),
    comment TEXT,
    PRIMARY KEY (date, id)
);
```

## 🔄 Data Flow

### Appointment Booking Flow
1. **Customer Input**: Name, phone, date, barberDatabaseEntity selection
2. **Validation**: Server-side validation of input data
3. **Conflict Check**: Verify appointmentDatabaseEntity slot availability
4. **Database Save**: Store appointmentDatabaseEntity in database
5. **Response**: Success/error message to userDatabaseEntity

### Admin Operations Flow
1. **Authentication**: Verify admin credentials
2. **Authorization**: Check admin role permissions
3. **Operation**: Perform requested action (CRUD)
4. **File Handling**: Process file uploads if applicable
5. **Database Update**: Persist changes
6. **Response**: Redirect with success/error message

## 🎨 Frontend Architecture

### Template Structure
- **Thymeleaf**: Server-side templating
- **Bootstrap**: Responsive CSS framework
- **JavaScript**: Client-side interactivity
- **Flatpickr**: Date/time picker component

### Key Templates
- `index.html`: Homepage with barberDatabaseEntity listings
- `login.html`: Authentication form
- `dashboard.html`: Barber dashboard
- `admin.html`: Administrative interface
- `access-denied.html`: Error page

### Static Resources
- **CSS**: Bootstrap, custom styles
- **JavaScript**: Form handling, AJAX calls
- **Images**: Barber photos, UI elements
- **Fonts**: Custom typography

## ⚙️ Configuration Management

### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/barbershop
spring.datasource.username=barbershop
spring.datasource.password=barbershop

# Branch Configuration
branches=Shumen,Sofia,Plovdiv

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### Environment-Specific Configuration
- **Development**: Local MySQL instance
- **Production**: Configured database credentials
- **Testing**: In-memory database (if needed)

## 🔧 Build and Deployment

### Maven Configuration
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.3</version>
</parent>
```

### Dependencies
- **Spring Boot Starters**: Web, Security, Data JPA, Thymeleaf
- **MySQL Connector**: Database connectivity
- **Lombok**: Code generation
- **Validation**: Input validation

### Build Process
1. **Compilation**: Java source compilation
2. **Resource Processing**: Static resource copying
3. **Dependency Resolution**: Maven dependency management
4. **Packaging**: JAR file creation
5. **Execution**: Spring Boot application startup

## 🧪 Testing Strategy

### Unit Testing
- **Service Layer**: Business logic testing
- **Repository Layer**: Data access testing
- **Controller Layer**: Request/response testing

### Integration Testing
- **Database Integration**: JPA operations
- **Security Integration**: Authentication/authorization
- **Web Integration**: End-to-end testing

### Test Configuration
```java
@SpringBootTest
@AutoConfigureTestDatabase
class BarberShopApplicationTests {
    // Test implementations
}
```

## 📊 Performance Considerations

### Database Optimization
- **Indexing**: Primary keys and foreign keys
- **Query Optimization**: Efficient JPA queries
- **Connection Pooling**: HikariCP configuration

### Caching Strategy
- **Static Resources**: Browser caching
- **Database Queries**: JPA query caching
- **Session Management**: Efficient session handling

### Scalability
- **Horizontal Scaling**: Multiple application instances
- **Database Scaling**: Read replicas if needed
- **Load Balancing**: Reverse proxy configuration

## 🔍 Monitoring and Logging

### Logging Configuration
```properties
logging.level.com.example.barbershop=DEBUG
logging.level.org.springframework.security=DEBUG
```

### Application Monitoring
- **Health Checks**: Spring Boot Actuator
- **Metrics**: Application performance metrics
- **Error Tracking**: Exception handling and logging

## 🚀 Deployment Considerations

### Production Environment
- **Database**: Production MySQL instance
- **Application Server**: Standalone JAR or container
- **Reverse Proxy**: Nginx or Apache
- **SSL/TLS**: HTTPS configuration

### Container Deployment
```dockerfile
FROM openjdk:17-jre-slim
COPY target/BarberShop-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://production-db:3306/barbershop
SPRING_DATASOURCE_USERNAME=prod_user
SPRING_DATASOURCE_PASSWORD=prod_password
```

---

This technical documentation provides a comprehensive overview of the BarberShop application's architecture, implementation details, and deployment considerations. For specific implementation questions, refer to the source code and API documentation. 