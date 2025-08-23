package com.example.barbershop.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.barbershop.entity.UserDatabaseEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserDatabaseEntity, String> {

    UserDatabaseEntity findByUsername(String username);

    UserDatabaseEntity findByBarberId(Integer id);
    
    @Query(value = "INSERT INTO users (username, role) VALUES (?1, ?2)", nativeQuery = true)
    @Modifying
    @Transactional
    void addUserRole(String username, String role);

    void deleteByUsername(String username);
    
    @Query(value = "UPDATE users SET role = ?2 WHERE username = ?1", nativeQuery = true)
    @Modifying
    @Transactional
    void updateUserRole(String username, String role);
    
    @Query(value = "SELECT role FROM users WHERE username = ?1", nativeQuery = true)
    String getUserRole(String username);
}
