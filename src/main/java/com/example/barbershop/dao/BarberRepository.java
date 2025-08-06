package com.example.barbershop.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.barbershop.entity.Barber;

@Repository
public interface BarberRepository extends JpaRepository<Barber, Long> {
    
}
