# Exception Handling System - BarberShop

## Overview

The BarberShop application now includes a comprehensive exception handling system that provides consistent error handling across all layers of the application. This system includes custom exceptions, validation utilities, and a global exception handler.

## Architecture

### Exception Hierarchy

```
BarberShopException (Base)
├── AppointmentException
├── BarberException
├── UserException
├── FileUploadException
└── ValidationException
```

## Custom Exceptions

### 1. BarberShopException (Base Class)

The base exception class for all custom exceptions in the application.

**Features:**
- Error code tracking
- Message and cause support
- Consistent error structure

**Usage:**
```java
throw new BarberShopException("Custom error message");
throw new BarberShopException("CUSTOM_ERROR_CODE", "Error message", cause);
```

### 2. AppointmentException

Handles appointment-related errors.

**Static Methods:**
- `notFound(String appointmentId)` - Appointment not found
- `alreadyExists(String barberId, String date)` - Appointment conflict
- `invalidDate(String date)` - Invalid date format
- `pastDate(String date)` - Past date booking attempt
- `deletionFailed(String appointmentId)` - Deletion failure

**Usage:**
```java
throw AppointmentException.notFound("appointment-123");
throw AppointmentException.alreadyExists("barber-1", "2024-01-15 10:00");
```

### 3. BarberException

Handles barber-related errors.

**Static Methods:**
- `notFound(Long barberId)` - Barber not found by ID
- `notFound(String barberName)` - Barber not found by name
- `alreadyExists(String email)` - Barber already exists
- `deletionFailed(Long barberId)` - Deletion failure
- `invalidData(String field)` - Invalid barber data

**Usage:**
```java
throw BarberException.notFound(1L);
throw BarberException.alreadyExists("barber@example.com");
```

### 4. UserException

Handles user-related errors.

**Static Methods:**
- `notFound(String username)` - User not found
- `alreadyExists(String username)` - User already exists
- `invalidCredentials()` - Invalid login credentials
- `accountDisabled(String username)` - Account disabled
- `insufficientPermissions(String username, String requiredRole)` - Permission denied
- `deletionFailed(String username)` - Deletion failure

**Usage:**
```java
throw UserException.notFound("john.doe");
throw UserException.invalidCredentials();
```

### 5. FileUploadException

Handles file upload-related errors.

**Static Methods:**
- `invalidFileType(String fileName, String allowedTypes)` - Invalid file type
- `fileTooLarge(String fileName, long maxSize)` - File too large
- `uploadFailed(String fileName)` - Upload failure
- `deletionFailed(String fileName)` - File deletion failure
- `invalidFileName(String fileName)` - Invalid filename

**Usage:**
```java
throw FileUploadException.invalidFileType("document.pdf", "JPG, PNG, GIF");
throw FileUploadException.fileTooLarge("image.jpg", 10485760);
```

### 6. ValidationException

Handles validation errors with detailed field information.

**Features:**
- Multiple validation errors support
- Field-specific error messages
- Structured error collection

**Static Methods:**
- `requiredField(String fieldName)` - Required field missing
- `invalidFormat(String fieldName, String expectedFormat)` - Invalid format
- `invalidLength(String fieldName, int minLength, int maxLength)` - Length validation
- `invalidEmail(String email)` - Invalid email format
- `invalidPhone(String phone)` - Invalid phone format

**Usage:**
```java
throw ValidationException.requiredField("email");
throw ValidationException.invalidEmail("invalid-email");
```

## Validation Utilities

### ValidationUtils Class

Provides static methods for common validation operations.

**Methods:**
- `validateRequired(String value, String fieldName)` - Required field validation
- `validateLength(String value, String fieldName, int minLength, int maxLength)` - Length validation
- `validateEmail(String email)` - Email format validation
- `validatePhone(String phone)` - Phone format validation
- `validateAppointmentDate(String dateStr)` - Date validation with past date check
- `validateBarberId(Long barberId)` - Barber ID validation
- `validateFileUpload(String fileName, long fileSize, long maxSize)` - File upload validation

**Usage:**
```java
ValidationUtils.validateRequired(name, "name");
ValidationUtils.validateEmail(email);
LocalDateTime dateTime = ValidationUtils.validateAppointmentDate(dateStr);
```

## Global Exception Handler

### GlobalExceptionHandler Class

Handles all exceptions centrally and provides consistent error responses.

**Features:**
- JSON error responses for REST endpoints
- Redirect handling for form submissions
- Structured error information
- Proper HTTP status codes
- Logging integration

**Error Response Format:**
```json
{
  "errorCode": "APPOINTMENT_ERROR",
  "message": "Appointment not found with ID: 123",
  "timestamp": "2024-01-15T10:30:00",
  "path": "/appointments/123",
  "details": {
    "errorType": "AppointmentException"
  }
}
```

**Handled Exception Types:**
- `BarberShopException` - Custom application exceptions
- `ValidationException` - Validation errors
- `MethodArgumentNotValidException` - Spring validation errors
- `MaxUploadSizeExceededException` - File size exceeded
- `AccessDeniedException` - Security access denied
- `AuthenticationException` - Authentication failures
- `Exception` - Generic exception fallback

## Controller Integration

### Form-Based Controllers

Controllers that handle form submissions use `RedirectAttributes` for error handling:

```java
@PostMapping("/appointments/booking")
public String booking(@RequestParam String name, 
                     @RequestParam String phone,
                     RedirectAttributes redirectAttributes) {
    try {
        // Validate inputs
        ValidationUtils.validateRequired(name, "name");
        ValidationUtils.validatePhone(phone);
        
        // Business logic
        appointmentService.save(appointment);
        
        redirectAttributes.addFlashAttribute("success", "Appointment booked successfully!");
    } catch (AppointmentException | ValidationException e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    
    return "redirect:/";
}
```

### REST Controllers

REST controllers automatically get JSON error responses:

```java
@GetMapping("/api/appointments/{id}")
public ResponseEntity<Appointment> getAppointment(@PathVariable String id) {
    // If exception is thrown, GlobalExceptionHandler returns JSON error
    Appointment appointment = appointmentService.findById(id);
    return ResponseEntity.ok(appointment);
}
```

## Service Layer Integration

### Exception Propagation

Services throw specific exceptions that are handled by the global handler:

```java
@Service
public class AppointmentServiceImpl {
    
    public Appointment findById(String id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> AppointmentException.notFound(id));
    }
    
    public Appointment save(Appointment appointment) {
        try {
            return appointmentRepository.save(appointment);
        } catch (Exception e) {
            throw new AppointmentException("Failed to save appointment", e);
        }
    }
}
```

## Testing Exception Handling

### Test Endpoints

The application includes test endpoints to verify exception handling:

- `GET /test/appointment-exception` - Tests AppointmentException
- `GET /test/barber-exception` - Tests BarberException
- `GET /test/user-exception` - Tests UserException
- `GET /test/validation-exception?email=test` - Tests ValidationException

### Example Test Response

```bash
curl http://localhost:8080/test/appointment-exception
```

Response:
```json
{
  "errorCode": "APPOINTMENT_ERROR",
  "message": "Appointment not found with ID: test-appointment-id",
  "timestamp": "2024-01-15T10:30:00",
  "path": "/test/appointment-exception",
  "details": {
    "errorType": "AppointmentException"
  }
}
```

## Best Practices

### 1. Use Specific Exceptions

Always use the most specific exception type:

```java
// Good
throw AppointmentException.notFound(id);

// Avoid
throw new BarberShopException("Appointment not found");
```

### 2. Validate Early

Validate inputs as early as possible in the request processing:

```java
@PostMapping("/appointments")
public String createAppointment(@RequestParam String name, 
                               @RequestParam String phone) {
    // Validate immediately
    ValidationUtils.validateRequired(name, "name");
    ValidationUtils.validatePhone(phone);
    
    // Then proceed with business logic
    // ...
}
```

### 3. Provide Meaningful Messages

Use descriptive error messages that help users understand the issue:

```java
// Good
throw ValidationException.invalidEmail("user@invalid");

// Avoid
throw new ValidationException("Invalid input");
```

### 4. Log Appropriately

The global exception handler logs all exceptions. Add additional logging in services when needed:

```java
@Service
public class AppointmentService {
    
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);
    
    public void deleteAppointment(String id) {
        try {
            appointmentRepository.deleteById(id);
            logger.info("Appointment {} deleted successfully", id);
        } catch (Exception e) {
            logger.error("Failed to delete appointment {}", id, e);
            throw AppointmentException.deletionFailed(id);
        }
    }
}
```

## Configuration

### Application Properties

Add these properties to `application.properties` for enhanced logging:

```properties
# Logging configuration
logging.level.com.example.barbershop.exception=DEBUG
logging.level.com.example.barbershop.controller=INFO
logging.level.com.example.barbershop.service=INFO

# File upload limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## Migration Guide

### From Generic Exception Handling

**Before:**
```java
try {
    appointmentService.save(appointment);
} catch (Exception e) {
    System.err.println("Error: " + e.getMessage());
    return "redirect:/?error=true";
}
```

**After:**
```java
try {
    appointmentService.save(appointment);
    redirectAttributes.addFlashAttribute("success", "Appointment saved!");
} catch (AppointmentException e) {
    redirectAttributes.addFlashAttribute("error", e.getMessage());
}
return "redirect:/";
```

### From Manual Validation

**Before:**
```java
if (name == null || name.trim().isEmpty()) {
    return "redirect:/?error=Name is required";
}
```

**After:**
```java
try {
    ValidationUtils.validateRequired(name, "name");
} catch (ValidationException e) {
    redirectAttributes.addFlashAttribute("error", e.getMessage());
    return "redirect:/";
}
```

## Security Considerations

### Error Information Exposure

The exception handling system is designed to provide useful error messages without exposing sensitive information:

- Database connection details are not exposed
- Internal system paths are not revealed
- Stack traces are logged but not sent to clients
- Generic messages are used for security-related errors

### Input Validation

All user inputs are validated to prevent:
- SQL injection
- XSS attacks
- File upload vulnerabilities
- Business logic bypasses

## Monitoring and Alerting

### Log Analysis

Monitor these log patterns for system health:

- High frequency of validation errors
- Repeated authentication failures
- File upload failures
- Database connection issues

### Metrics

Consider adding metrics for:
- Exception frequency by type
- Response times for error handling
- Success/failure rates by endpoint

## Conclusion

The exception handling system provides a robust foundation for error management in the BarberShop application. It ensures consistent error responses, proper logging, and a good user experience while maintaining security and performance.

For questions or issues with the exception handling system, please refer to the application logs or contact the development team.
