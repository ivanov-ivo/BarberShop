package com.example.barbershop.exception;

public class BarberShopException extends RuntimeException {
    
    private final String errorCode;
    
    public BarberShopException(String message) {
        super(message);
        this.errorCode = "BARBERSHOP_ERROR";
    }
    
    public BarberShopException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BARBERSHOP_ERROR";
    }
    
    public BarberShopException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BarberShopException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
