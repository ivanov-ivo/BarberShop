package com.example.barbershop.exception;

import java.util.List;
import java.util.ArrayList;

public class ValidationException extends BarberShopException {
    
    private final List<String> validationErrors;
    
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
        this.validationErrors = new ArrayList<>();
        this.validationErrors.add(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super("VALIDATION_ERROR", message, cause);
        this.validationErrors = new ArrayList<>();
        this.validationErrors.add(message);
    }
    
    public ValidationException(List<String> validationErrors) {
        super("VALIDATION_ERROR", "Validation failed: " + String.join(", ", validationErrors));
        this.validationErrors = new ArrayList<>(validationErrors);
    }
    
    public ValidationException(String field, String error) {
        super("VALIDATION_ERROR", "Validation failed for field '" + field + "': " + error);
        this.validationErrors = new ArrayList<>();
        this.validationErrors.add(field + ": " + error);
    }
    
    // Defensive copy to prevent exposure of internal representation
    public List<String> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }
    
    public static ValidationException requiredField(String fieldName) {
        return new ValidationException(fieldName, "Field is required");
    }
    
    public static ValidationException invalidFormat(String fieldName, String expectedFormat) {
        return new ValidationException(fieldName, "Invalid format. Expected: " + expectedFormat);
    }
    
    public static ValidationException invalidLength(String fieldName, int minLength, int maxLength) {
        return new ValidationException(fieldName, "Length must be between " + minLength + " and " + maxLength + " characters");
    }
    
    public static ValidationException invalidEmail(String email) {
        return new ValidationException("email", "Invalid email format: " + email);
    }
    
    public static ValidationException invalidPhone(String phone) {
        return new ValidationException("phone", "Invalid phone number format: " + phone);
    }
}
