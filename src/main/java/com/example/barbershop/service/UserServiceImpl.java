package com.example.barbershop.service;

import com.example.barbershop.dao.UserRepository;
import com.example.barbershop.entity.UserDatabaseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserServiceInterface {
    
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDatabaseEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDatabaseEntity findById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public UserDatabaseEntity save(UserDatabaseEntity userDatabaseEntity) {
        return userRepository.save(userDatabaseEntity);
    }

    @Override
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDatabaseEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void addUserRole(String username, String role) {
        userRepository.addUserRole(username, role);
    }

    @Override
    public void deleteUserRole(String username) {
        userRepository.deleteUserRole(username);
    }

    @Override
    public void updateUserRole(String username, String role) {
        userRepository.updateUserRole(username, role);
    }

    @Override
    public String getUserRole(String username) {
        return userRepository.getUserRole(username);
    }

    @Override
    public UserDatabaseEntity findByBarberId(Long barberId) {
        return userRepository.findById(barberId);
    }
} 