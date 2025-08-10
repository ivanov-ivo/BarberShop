# API Documentation - BarberShop

## 📋 Overview

This document provides comprehensive API documentation for the BarberShop application. The API follows RESTful principles and uses Spring Boot controllers to handle HTTP requests.

## 🔗 Base URL

```
http://localhost:8080
```

## 🔐 Authentication

The application uses form-based authentication with Spring Security. Most endpoints require authentication except for public endpoints.

### Authentication Flow
1. Submit credentials to `/login`
2. Receive JSESSIONID cookie
3. Include cookie in subsequent requests

## 📡 API Endpoints

### Public Endpoints

#### 1. Homepage
**GET** `/`

Displays the main homepage with barber listings.

**Response:**
- **Content-Type**: `text/html`
- **Template**: `index.html`
- **Data**: List of barbers, branches

**Example Response:**
```html
<!DOCTYPE html>
<html>
<head>
    <title>Gentlemen's Barber Shop</title>
</head>
<body>
    <!-- Barber listings and booking forms -->
</body>
</html>
```

#### 2. Login Page
**GET** `/login`

Displays the login form.

**Response:**
- **Content-Type**: `text/html`
- **Template**: `login.html`

**Query Parameters:**
- `error` (optional): Display error message
- `logout` (optional): Display logout message

#### 3. Book Appointment
**POST** `/appointments/booking`

Creates a new appointment booking.

**Request Body:**
```form-data
name: string (required) - Customer name
phone: string (required) - Customer phone number
date: string (required) - Appointment date/time (YYYY-MM-DD HH:mm)
message: string (optional) - Additional comments
barber: long (required) - Barber ID
```

**Response:**
- **Status**: 302 Redirect
- **Location**: `/` (homepage)
- **Flash Attributes**: Success/error message

**Example Request:**
```bash
curl -X POST http://localhost:8080/appointments/booking \
  -F "name=John Doe" \
  -F "phone=+1234567890" \
  -F "date=2024-01-15 14:30" \
  -F "message=Haircut and beard trim" \
  -F "barber=1"
```

### Protected Endpoints

#### 4. Barber Dashboard
**GET** `/dashboard`

Displays the barber's dashboard with appointments.

**Authentication:** Required (ROLE_BARBER, ROLE_ADMIN)

**Response:**
- **Content-Type**: `text/html`
- **Template**: `dashboard.html`
- **Data**: User info, barber info, appointments

#### 5. Admin Panel
**GET** `/admin`

Displays the administrative interface.

**Authentication:** Required (ROLE_ADMIN)

**Response:**
- **Content-Type**: `text/html`
- **Template**: `admin.html`
- **Data**: User info, barber info, all barbers, appointments

#### 6. Add Barber
**POST** `/admin/addBarber`

Creates a new barber account.

**Authentication:** Required (ROLE_ADMIN)

**Request Body:**
```form-data
barberName: string (required) - Barber's full name
barberPhoto: file (required) - Profile photo
barberBranch: string (required) - Branch location
barberInformation: string (optional) - Additional information
barberEmail: string (required) - Login email
barberPassword: string (required) - Login password
barberRole: string (required) - User role (ADMIN/BARBER)
```

**Response:**
- **Status**: 302 Redirect
- **Location**: `/admin`

**Example Request:**
```bash
curl -X POST http://localhost:8080/admin/addBarber \
  -F "barberName=Mike Johnson" \
  -F "barberPhoto=@photo.jpg" \
  -F "barberBranch=Sofia" \
  -F "barberInformation=Specializes in modern cuts" \
  -F "barberEmail=mike@barbershop.com" \
  -F "barberPassword=password123" \
  -F "barberRole=BARBER"
```

#### 7. Edit Barber
**POST** `/admin/editBarber/{barberId}`

Updates an existing barber's information.

**Authentication:** Required (ROLE_ADMIN)

**Path Parameters:**
- `barberId`: long (required) - Barber ID

**Request Body:**
```form-data
barberName: string (required) - Barber's full name
barberPhoto: file (optional) - New profile photo
barberBranch: string (required) - Branch location
barberInformation: string (optional) - Additional information
barberEmail: string (optional) - Login email
barberPassword: string (optional) - Login password
barberRole: string (optional) - User role
```

**Response:**
- **Status**: 302 Redirect
- **Location**: `/admin`

#### 8. Delete Barber
**POST** `/admin/deleteBarber`

Removes a barber from the system.

**Authentication:** Required (ROLE_ADMIN)

**Request Body:**
```form-data
id: long (required) - Barber ID to delete
```

**Response:**
- **Status**: 302 Redirect
- **Location**: `/admin`

#### 9. Get Barber Details
**GET** `/admin/getBarber/{id}`

Retrieves barber information as JSON.

**Authentication:** Required (ROLE_ADMIN)

**Path Parameters:**
- `id`: long (required) - Barber ID

**Response:**
- **Content-Type**: `application/json`

**Example Response:**
```json
{
  "id": 1,
  "name": "Mike Johnson",
  "photo": "/uploads/barber1.jpg",
  "branch": "Sofia",
  "information": "Specializes in modern cuts"
}
```

#### 10. Get Barber Appointments
**GET** `/admin/appointments/{id}`

Retrieves appointments for a specific barber.

**Authentication:** Required (ROLE_ADMIN)

**Path Parameters:**
- `id`: long (required) - Barber ID

**Response:**
- **Content-Type**: `application/json`

**Example Response:**
```json
[
  {
    "date": "2024-01-15T14:30:00.000+00:00",
    "name": "John Doe",
    "phone": "+1234567890",
    "barberId": 1,
    "comment": "Haircut and beard trim"
  }
]
```

#### 11. Delete Appointment (Admin)
**POST** `/admin/appointments/{id}/delete`

Deletes a specific appointment.

**Authentication:** Required (ROLE_ADMIN)

**Path Parameters:**
- `id`: long (required) - Barber ID

**Request Body:**
```form-data
date: timestamp (required) - Appointment date/time
```

**Response:**
- **Content-Type**: `application/json`
- **Status**: 200 OK

#### 12. Edit Appointment
**POST** `/appointments/edit`

Updates an existing appointment.

**Authentication:** Required (ROLE_BARBER, ROLE_ADMIN)

**Request Body:**
```form-data
originalDate: timestamp (required) - Original appointment date
name: string (required) - Customer name
phone: string (required) - Customer phone
newDate: string (required) - New appointment date (YYYY-MM-DD HH:mm)
comment: string (optional) - Additional comments
barberId: long (required) - Barber ID
```

**Response:**
- **Status**: 302 Redirect
- **Location**: `/dashboard`

#### 13. Delete Appointment
**POST** `/appointments/delete`

Deletes an appointment.

**Authentication:** Required (ROLE_BARBER, ROLE_ADMIN)

**Request Body:**
```form-data
appointmentDate: timestamp (required) - Appointment date/time
barberId: long (required) - Barber ID
```

**Response:**
- **Status**: 302 Redirect
- **Location**: `/dashboard`

## 📊 Data Models

### User Model
```json
{
  "username": "string",
  "password": "string (encrypted)",
  "enabled": "boolean",
  "barberId": "long",
  "role": "string (ADMIN/BARBER)"
}
```

### Barber Model
```json
{
  "id": "long",
  "name": "string",
  "photo": "string (file path)",
  "branch": "string",
  "information": "string"
}
```

### Appointment Model
```json
{
  "date": "timestamp",
  "name": "string",
  "phone": "string",
  "barberId": "long",
  "comment": "string"
}
```

## 🔒 Security Headers

The application includes the following security headers:

- **X-Frame-Options**: DENY
- **X-Content-Type-Options**: nosniff
- **X-XSS-Protection**: 1; mode=block
- **Strict-Transport-Security**: max-age=31536000; includeSubDomains

## 📝 Error Handling

### HTTP Status Codes

- **200 OK**: Successful request
- **302 Found**: Redirect response
- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

### Error Response Format

For JSON endpoints:
```json
{
  "error": "string",
  "message": "string",
  "timestamp": "timestamp"
}
```

For HTML endpoints:
- Error messages displayed in templates
- Flash attributes for temporary messages

## 🔄 Session Management

### Session Configuration
- **Session Timeout**: 30 minutes (configurable)
- **Session Cookie**: JSESSIONID
- **Secure**: HTTPS only in production
- **HttpOnly**: true

### Session Attributes
- **SPRING_SECURITY_CONTEXT**: Authentication context
- **User Information**: Current user details

## 📁 File Upload

### Supported Formats
- **Images**: JPG, PNG, GIF
- **Max Size**: 10MB per file
- **Storage**: Local file system

### Upload Endpoints
- `/admin/addBarber`: Barber photo upload
- `/admin/editBarber/{id}`: Photo update

### File Storage
- **Base Path**: `/uploads/`
- **Naming**: UUID-based file names
- **Access**: Public via static resource mapping

## 🧪 Testing Endpoints

### Health Check
**GET** `/actuator/health`

Returns application health status.

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

## 📈 Performance Considerations

### Response Times
- **Static Resources**: < 100ms
- **Database Queries**: < 500ms
- **File Uploads**: < 2s (depending on file size)

### Caching
- **Static Resources**: Browser caching enabled
- **Database Queries**: JPA query caching
- **Session Data**: In-memory session storage

## 🔧 Configuration

### Environment Variables
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/barbershop
SPRING_DATASOURCE_USERNAME=barbershop
SPRING_DATASOURCE_PASSWORD=barbershop
BRANCHES=Shumen,Sofia,Plovdiv
```

### Application Properties
```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# Database Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## 📚 Usage Examples

### Complete Booking Flow
```bash
# 1. Visit homepage
curl -X GET http://localhost:8080/

# 2. Book appointment
curl -X POST http://localhost:8080/appointments/booking \
  -F "name=John Doe" \
  -F "phone=+1234567890" \
  -F "date=2024-01-15 14:30" \
  -F "message=Haircut and beard trim" \
  -F "barber=1"
```

### Admin Operations Flow
```bash
# 1. Login (browser session)
# 2. Add new barber
curl -X POST http://localhost:8080/admin/addBarber \
  -H "Cookie: JSESSIONID=..." \
  -F "barberName=Mike Johnson" \
  -F "barberPhoto=@photo.jpg" \
  -F "barberBranch=Sofia" \
  -F "barberEmail=mike@barbershop.com" \
  -F "barberPassword=password123" \
  -F "barberRole=BARBER"

# 3. Get barber details
curl -X GET http://localhost:8080/admin/getBarber/1 \
  -H "Cookie: JSESSIONID=..."
```

---

This API documentation provides comprehensive information about all endpoints, request/response formats, and usage examples. For additional support, refer to the technical documentation or contact the development team. 