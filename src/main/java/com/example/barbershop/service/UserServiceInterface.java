package com.example.barbershop.service;

import com.example.barbershop.entity.UserDatabaseEntity;

import java.util.List;

public interface UserServiceInterface {

    List<UserDatabaseEntity> findAll();

    UserDatabaseEntity findById(String id);

    UserDatabaseEntity save(UserDatabaseEntity userDatabaseEntity);

    void deleteById(String id);

    UserDatabaseEntity findByUsername(String username);

    void addUserRole(String username, String role);

    void deleteUserRole(String username);

    void updateUserRole(String username, String role);

    String getUserRole(String username);

    UserDatabaseEntity findByBarberId(Long barberId);
}
