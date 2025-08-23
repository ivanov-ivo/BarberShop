package com.example.barbershop.exception;

public class AppointmentException extends BarberShopException {
    
    public AppointmentException(String message) {
        super("APPOINTMENT_ERROR", message);
    }
    
    public AppointmentException(String message, Throwable cause) {
        super("APPOINTMENT_ERROR", message, cause);
    }

    public static AppointmentException notFound(String date, String barberId) {
        return new AppointmentException("Appointment not found for date: " + date + " and barber ID: " + barberId);
    }

    public static AppointmentException notFound(String id) {
        return new AppointmentException("Appointment not found for ID: " + id);
    }

    public static AppointmentException alreadyExists(String barberId, String date) {
        return new AppointmentException("Appointment already exists for barber " + barberId + " at " + date);
    }
    
    public static AppointmentException invalidDate(String date) {
        return new AppointmentException("Invalid appointment date: " + date);
    }
    
    public static AppointmentException pastDate(String date) {
        return new AppointmentException("Cannot book appointment in the past: " + date);
    }
    
    public static AppointmentException deletionFailed(String appointmentId) {
        return new AppointmentException("Failed to delete appointment: " + appointmentId);
    }
}
