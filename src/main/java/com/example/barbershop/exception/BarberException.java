package com.example.barbershop.exception;

public class BarberException extends BarberShopException {
    
    public BarberException(String message) {
        super("BARBER_ERROR", message);
    }
    
    public BarberException(String message, Throwable cause) {
        super("BARBER_ERROR", message, cause);
    }
    
    public static BarberException notFound(Integer barberId) {
        return new BarberException("Barber not found with ID: " + barberId);
    }
    
    public static BarberException deletionFailed(Integer barberId) {
        return new BarberException("Failed to delete barber: " + barberId);
    }
    
    public static BarberException invalidData(String field) {
        return new BarberException("Invalid barber data for field: " + field);
    }
}
