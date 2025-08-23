package com.example.barbershop.service;

import com.example.barbershop.dao.UserRepository;
import com.example.barbershop.entity.UserDatabaseEntity;
import com.example.barbershop.exception.UserException;
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
        return userRepository.findById(id)
            .orElseThrow(() -> UserException.notFound(id));
    }

    @Override
    public UserDatabaseEntity save(UserDatabaseEntity userDatabaseEntity) {
        try {
            return userRepository.save(userDatabaseEntity);
        } catch (Exception e) {
            throw new UserException("Failed to save user", e);
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw UserException.deletionFailed(id);
        }
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
    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
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
    public UserDatabaseEntity findByBarberId(Integer barberId) {
        return userRepository.findByBarberId(barberId);
    }
} 