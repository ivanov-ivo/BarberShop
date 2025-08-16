package com.example.barbershop.service;

import com.example.barbershop.dao.BarberRepository;
import com.example.barbershop.entity.Barber;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarberService {
    
    private BarberRepository barberRepository;

    public BarberService(BarberRepository barberRepository) {
        this.barberRepository = barberRepository;
    }
    
    public List<Barber> findAll() {
        return barberRepository.findAll();
    }
    
    public Barber findById(Long id) {
        return barberRepository.findById(id).orElse(null);
    }
    
    public Barber save(Barber barber) {
        return barberRepository.save(barber);
    }
    
    public void deleteById(Long id) {
        barberRepository.deleteById(id);
    }
} 