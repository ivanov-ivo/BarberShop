package com.example.barbershop.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9\\s\\-\\(\\)]{8,15}$"
    );
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    

    public static void validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw ValidationException.requiredField(fieldName);
        }
    }
    

    public static void validateLength(String value, String fieldName, int minLength, int maxLength) {
        if (value != null && (value.length() < minLength || value.length() > maxLength)) {
            throw ValidationException.invalidLength(fieldName, minLength, maxLength);
        }
    }
    

    public static void validateEmail(String email) {
        if (email != null && !email.trim().isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            throw ValidationException.invalidEmail(email);
        }
    }
    

    public static void validatePhone(String phone) {
        if (phone != null && !phone.trim().isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            throw ValidationException.invalidPhone(phone);
        }
    }
    

    public static LocalDateTime validateAppointmentDate(String dateStr) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, DATE_TIME_FORMATTER);
            
            if (dateTime.isBefore(LocalDateTime.now())) {
                throw AppointmentException.pastDate(dateStr);
            }
            
            return dateTime;
        } catch (DateTimeParseException e) {
            throw AppointmentException.invalidDate(dateStr);
        }
    }
    

    public static void validateBarberId(Integer barberId) {
        if (barberId == null || barberId <= 0) {
            throw ValidationException.invalidFormat("barberId", "positive number");
        }
    }
    

    public static void validateFileUpload(String fileName, long fileSize, long maxSize) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw FileUploadException.invalidFileName(fileName);
        }
        
        if (fileSize > maxSize) {
            throw FileUploadException.fileTooLarge(fileName, maxSize);
        }
        
        String extension = getFileExtension(fileName);
        if (!isValidImageExtension(extension)) {
            throw FileUploadException.invalidFileType(fileName, "JPG, PNG, GIF");
        }
    }
    
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
    
    private static boolean isValidImageExtension(String extension) {
        return extension.equals("jpg") || extension.equals("jpeg") || 
               extension.equals("png") || extension.equals("gif");
    }
}
