package com.example.barbershop.exception;

public class UserException extends BarberShopException {
    
    public UserException(String message) {
        super("USER_ERROR", message);
    }
    
    public UserException(String message, Throwable cause) {
        super("USER_ERROR", message, cause);
    }
    
    public static UserException notFound(String username) {
        return new UserException("User not found with username: " + username);
    }
    
    public static UserException alreadyExists(String username) {
        return new UserException("User already exists with username: " + username);
    }
    
    public static UserException invalidCredentials() {
        return new UserException("Invalid username or password");
    }
    
    public static UserException accountDisabled(String username) {
        return new UserException("Account is disabled for user: " + username);
    }
    
    public static UserException insufficientPermissions(String username, String requiredRole) {
        return new UserException("User " + username + " does not have required role: " + requiredRole);
    }
    
    public static UserException deletionFailed(String username) {
        return new UserException("Failed to delete user: " + username);
    }
}
