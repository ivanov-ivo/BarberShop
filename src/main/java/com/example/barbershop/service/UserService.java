package com.example.barbershop.service;

import com.example.barbershop.dao.UserRepository;
import com.example.barbershop.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public void addUserRole(String username, String role) {
        userRepository.addUserRole(username, role);
    }
    
    public void deleteUserRole(String username) {
        userRepository.deleteUserRole(username);
    }
    
    public void updateUserRole(String username, String role) {
        userRepository.updateUserRole(username, role);
    }
    
    public String getUserRole(String username) {
        return userRepository.getUserRole(username);
    }

    public User findByBarberId(Long barberId) {
        return userRepository.findById(barberId);
    }
} 