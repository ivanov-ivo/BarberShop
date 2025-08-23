package com.example.barbershop.service;

import com.example.barbershop.dao.BarberRepository;
import com.example.barbershop.entity.BarberDatabaseEntity;
import com.example.barbershop.exception.BarberException;
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
    public BarberDatabaseEntity findById(Integer id) {
        return barberRepository.findById(id)
            .orElseThrow(() -> BarberException.notFound(id));
    }

    @Override
    public BarberDatabaseEntity save(BarberDatabaseEntity barberDatabaseEntity) {
        try {
            return barberRepository.save(barberDatabaseEntity);
        } catch (Exception e) {
            throw new BarberException("Failed to save barber", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        try {
            barberRepository.deleteById(id);
        } catch (Exception e) {
            throw BarberException.deletionFailed(id);
        }
    }
} 