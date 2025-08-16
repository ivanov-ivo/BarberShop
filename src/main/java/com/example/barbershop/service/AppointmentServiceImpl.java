package com.example.barbershop.service;

import com.example.barbershop.dao.AppointmentRepository;
import com.example.barbershop.entity.AppointmentDatabaseEntity;
import org.springframework.stereotype.Service;
import com.example.barbershop.entity.AppointmentId;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentServiceInterface {
    
    private final AppointmentRepository appointmentRepository;
    
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<AppointmentDatabaseEntity> findAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public AppointmentDatabaseEntity findById(AppointmentId id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    @Override
    public AppointmentDatabaseEntity save(AppointmentDatabaseEntity appointmentDatabaseEntity) {
        return appointmentRepository.save(appointmentDatabaseEntity);
    }

    @Override
    public void deleteById(AppointmentId id) {
        appointmentRepository.deleteById(id);
    }

    @Override
    public List<AppointmentDatabaseEntity> findByBarberId(Long barberId) {
        return appointmentRepository.findByBarberId(barberId);
    }

    @Override
    public AppointmentDatabaseEntity findByBarberIdAndDate(Long barberId, Timestamp date) {
        return appointmentRepository.findByBarberIdAndDate(barberId, date);
    }

    @Override
    @Transactional
    public void deleteAppointmentsOlderThan10Days() {
        LocalDateTime cutoffLocalDateTime = LocalDateTime.now().minusDays(10);
        Timestamp cutoffTimestamp = Timestamp.valueOf(cutoffLocalDateTime);
        appointmentRepository.deleteAppointmentsOlderThan(cutoffTimestamp);
    }
    
} 