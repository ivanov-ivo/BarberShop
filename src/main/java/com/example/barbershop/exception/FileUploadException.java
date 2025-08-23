package com.example.barbershop.exception;

public class FileUploadException extends BarberShopException {
    
    public FileUploadException(String message) {
        super("FILE_UPLOAD_ERROR", message);
    }
    
    public FileUploadException(String message, Throwable cause) {
        super("FILE_UPLOAD_ERROR", message, cause);
    }
    
    public static FileUploadException invalidFileType(String fileName, String allowedTypes) {
        return new FileUploadException("Invalid file type for " + fileName + ". Allowed types: " + allowedTypes);
    }
    
    public static FileUploadException fileTooLarge(String fileName, long maxSize) {
        return new FileUploadException("File " + fileName + " is too large. Maximum size: " + maxSize + " bytes");
    }
    
    public static FileUploadException uploadFailed(String fileName) {
        return new FileUploadException("Failed to upload file: " + fileName);
    }
    
    public static FileUploadException deletionFailed(String fileName) {
        return new FileUploadException("Failed to delete file: " + fileName);
    }
    
    public static FileUploadException invalidFileName(String fileName) {
        return new FileUploadException("Invalid filename: " + fileName);
    }
}
