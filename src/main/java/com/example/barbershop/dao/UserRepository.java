package com.example.barbershop.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.barbershop.entity.UserDatabaseEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserDatabaseEntity, String> {

    @Query(value = "SELECT * FROM UserDatabaseEntity WHERE username = ?1", nativeQuery = true)
    UserDatabaseEntity findByUsername(String username);

    @Query(value = "SELECT * FROM UserDatabaseEntity WHERE id = ?1", nativeQuery = true)
    UserDatabaseEntity findById(Long id);
    
    @Query(value = "INSERT INTO UserDatabaseEntity (username, role) VALUES (?1, ?2)", nativeQuery = true)
    @Modifying
    @Transactional
    void addUserRole(String username, String role);
    
    @Query(value = "DELETE FROM UserDatabaseEntity WHERE username = ?1", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteUserRole(String username);
    
    @Query(value = "UPDATE UserDatabaseEntity SET role = ?2 WHERE username = ?1", nativeQuery = true)
    @Modifying
    @Transactional
    void updateUserRole(String username, String role);
    
    @Query(value = "SELECT role FROM UserDatabaseEntity WHERE username = ?1", nativeQuery = true)
    String getUserRole(String username);
}
