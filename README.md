# BarberShop - Appointment Management System

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Installation & Setup](#installation--setup)
- [Database Configuration](#database-configuration)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [File Structure](#file-structure)
- [Contributing](#contributing)

## 🎯 Overview

BarberShop is a comprehensive web application for managing barbershop appointments. It provides a modern, user-friendly interface for customers to book appointments with barbers, while offering administrative tools for managing barbers, appointments, and system operations.

The application supports multiple branches (Shumen, Sofia, Plovdiv) and includes role-based access control with different user types (Admin, Barber).

## ✨ Features

### Customer Features
- **Appointment Booking**: Easy-to-use appointment booking system with date/time picker
- **Barber Selection**: Browse and select from available barbers at different branches
- **Real-time Availability**: View available time slots for each barber
- **Responsive Design**: Mobile-friendly interface for booking on any device

### Barber Features
- **Dashboard**: View and manage personal appointments
- **Appointment Management**: Edit, delete, and reschedule appointments
- **Profile Management**: Update personal information and photo

### Admin Features
- **Barber Management**: Add, edit, and delete barbers
- **User Management**: Create and manage user accounts
- **Appointment Oversight**: View all appointments across the system
- **File Upload**: Manage barber photos and profile images
- **Branch Management**: Configure multiple locations

### System Features
- **Security**: Role-based authentication and authorization
- **Scheduling**: Automated cleanup of old appointments
- **File Management**: Secure file upload and storage
- **Database**: MySQL-based data persistence

## 🛠 Technology Stack

### Backend
- **Java 17**: Core programming language
- **Spring Boot 3.5.3**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database access layer
- **Spring Validation**: Input validation
- **Lombok**: Code generation and boilerplate reduction

### Frontend
- **Thymeleaf**: Server-side templating engine
- **Bootstrap**: CSS framework for responsive design
- **JavaScript**: Client-side interactivity
- **Flatpickr**: Date and time picker component

### Database
- **MySQL**: Primary database
- **JPA/Hibernate**: Object-relational mapping

### Build Tool
- **Maven**: Dependency management and build automation

## 📁 Project Structure

```
BarberShop/
├── src/
│   ├── main/
│   │   ├── java/com/example/barbershop/
│   │   │   ├── controller/          # REST controllers
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── service/             # Business logic
│   │   │   ├── dao/                 # Data access objects
│   │   │   ├── security/            # Security configuration
│   │   │   └── BarberShopApplication.java
│   │   └── resources/
│   │       ├── templates/           # Thymeleaf templates
│   │       ├── static/              # Static assets (CSS, JS, images)
│   │       ├── db/                  # Database migrations
│   │       └── application.properties
│   └── test/                        # Test files
├── target/                          # Compiled output
├── pom.xml                          # Maven configuration
└── README.md                        # This file
```

## 🚀 Installation & Setup

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

### Step 1: Clone the Repository
```bash
git clone <repository-url>
cd BarberShop
```

### Step 2: Database Setup
1. Create a MySQL database named `barbershop`
2. Create a user with appropriate permissions:
```sql
CREATE DATABASE barbershop;
CREATE USER 'barbershop'@'localhost' IDENTIFIED BY 'barbershop';
GRANT ALL PRIVILEGES ON barbershop.* TO 'barbershop'@'localhost';
FLUSH PRIVILEGES;
```

### Step 3: Configure Application
Update `src/main/resources/application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/barbershop
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Step 4: Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## 🗄 Database Configuration

### Database Schema

The application uses the following main tables:

#### Users Table
- `username` (Primary Key): User login credentials
- `password`: Encrypted password
- `enabled`: Account status
- `id`: Associated barber ID
- `role`: User role (ADMIN, BARBER)

#### Barbers Table
- `id` (Primary Key): Auto-generated barber ID
- `name`: Barber's full name
- `photo`: Profile photo path
- `branch`: Branch location
- `information`: Additional information

#### Appointments Table
- `date` (Primary Key): Appointment date/time
- `id` (Primary Key): Barber ID
- `name`: Customer name
- `phone`: Customer phone number
- `comment`: Additional notes

## 📖 Usage

### For Customers
1. Visit the homepage at `http://localhost:8080`
2. Browse available barbers by branch
3. Select a barber and click "Book Appointment"
4. Fill in your details and select preferred date/time
5. Submit the booking

### For Barbers
1. Login with your credentials at `/login`
2. Access your dashboard at `/dashboard`
3. View and manage your appointments
4. Edit appointment details as needed

### For Administrators
1. Login with admin credentials
2. Access admin panel at `/admin`
3. Manage barbers, users, and appointments
4. Upload barber photos and manage system settings

## 🔌 API Endpoints

### Public Endpoints
- `GET /` - Homepage
- `GET /login` - Login page
- `POST /appointments/booking` - Book appointment

### Protected Endpoints
- `GET /dashboard` - Barber dashboard (ROLE_BARBER, ROLE_ADMIN)
- `GET /admin` - Admin panel (ROLE_ADMIN)
- `POST /admin/addBarber` - Add new barber (ROLE_ADMIN)
- `POST /admin/editBarber/{id}` - Edit barber (ROLE_ADMIN)
- `POST /admin/deleteBarber` - Delete barber (ROLE_ADMIN)
- `POST /appointments/delete` - Delete appointment
- `POST /appointments/edit` - Edit appointment

## 🔒 Security

### Authentication
- Form-based login with Spring Security
- BCrypt password encryption
- Session management with JSESSIONID

### Authorization
- Role-based access control (ADMIN, BARBER)
- URL-based security rules
- Custom authentication success handler

### Security Features
- CSRF protection (configurable)
- Password encoding with BCrypt
- Session invalidation on logout
- Access denied page for unauthorized requests

## 🛡️ Exception Handling

### Custom Exception System
- Comprehensive exception hierarchy with specific exception types
- Global exception handler for consistent error responses
- Validation utilities for input validation
- Proper error logging and user feedback

### Exception Types
- `AppointmentException` - Appointment-related errors
- `BarberException` - Barber management errors
- `UserException` - User authentication and management errors
- `FileUploadException` - File upload and storage errors
- `ValidationException` - Input validation errors

### Features
- JSON error responses for REST endpoints
- Flash message support for form submissions
- Structured error information with error codes
- Security-conscious error messages
- Comprehensive logging and monitoring support

For detailed information, see [Exception Handling Documentation](docs/EXCEPTION_HANDLING.md)

## 📂 File Structure Details

### Controllers
- `IndexController`: Handles homepage and public views
- `LoginController`: Manages authentication
- `DashboardController`: Barber dashboard functionality
- `AdminController`: Administrative operations
- `AppointmentsController`: Appointment CRUD operations

### Services
- `UserService`: User management operations
- `BarberService`: Barber-related business logic
- `AppointmentService`: Appointment management
- `FileUploadService`: File handling and storage
- `DeleteOldAppointmentsScheduler`: Automated cleanup

### Entities
- `User`: User account information
- `Barber`: Barber profile data
- `Appointment`: Appointment details
- `BarberId`: Composite key for appointments
- `LoginInput`: Login form data

### Repositories
- `UserRepository`: User data access
- `BarberRepository`: Barber data access
- `AppointmentRepository`: Appointment data access

## 🔧 Configuration

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

### Security Configuration
- Public access to static resources
- Role-based URL protection
- Custom login page and success handler
- Logout configuration with session cleanup

## 🧪 Testing

Run tests using Maven:
```bash
mvn test
```

## 📝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions, please contact the development team or create an issue in the repository.

---

**Note**: This application is designed for barbershop management and includes features for appointment booking, barber management, and administrative oversight. Make sure to configure the database and security settings according to your specific requirements before deployment. 