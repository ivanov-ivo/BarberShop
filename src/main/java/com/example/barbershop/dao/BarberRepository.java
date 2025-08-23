package com.example.barbershop.dao;

import com.example.barbershop.entity.BarberDatabaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarberRepository extends JpaRepository<BarberDatabaseEntity, Integer> {
    
}
