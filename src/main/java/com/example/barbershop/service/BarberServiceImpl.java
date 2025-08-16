package com.example.barbershop.service;

import com.example.barbershop.dao.BarberRepository;
import com.example.barbershop.entity.BarberDatabaseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarberServiceImpl implements BarberServiceInterface {
    
    private BarberRepository barberRepository;

    public BarberServiceImpl(BarberRepository barberRepository) {
        this.barberRepository = barberRepository;
    }

    @Override
    public List<BarberDatabaseEntity> findAll() {
        return barberRepository.findAll();
    }

    @Override
    public BarberDatabaseEntity findById(Long id) {
        return barberRepository.findById(id).orElse(null);
    }

    @Override
    public BarberDatabaseEntity save(BarberDatabaseEntity barberDatabaseEntity) {
        return barberRepository.save(barberDatabaseEntity);
    }

    @Override
    public void deleteById(Long id) {
        barberRepository.deleteById(id);
    }
} 