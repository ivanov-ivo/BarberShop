package com.example.barbershop.service;

import com.example.barbershop.dao.AppointmentRepository;
import com.example.barbershop.entity.AppointmentDatabaseEntity;
import com.example.barbershop.exception.AppointmentException;
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
        return appointmentRepository.findById(id)
            .orElseThrow(() -> AppointmentException.notFound(id.getDate().toString(), id.getBarberId().toString()));
    }

    @Override
    public AppointmentDatabaseEntity save(AppointmentDatabaseEntity appointmentDatabaseEntity) {
        try {
            return appointmentRepository.save(appointmentDatabaseEntity);
        } catch (Exception e) {
            throw new AppointmentException("Failed to save appointment", e);
        }
    }

    @Override
    public void deleteById(AppointmentId id) {
        try {
            appointmentRepository.deleteById(id);
        } catch (Exception e) {
            throw AppointmentException.deletionFailed(id.toString());
        }
    }

    @Override
    public List<AppointmentDatabaseEntity> findByBarberId(Integer barberId) {
        return appointmentRepository.findByBarberId(barberId);
    }

    @Override
    public AppointmentDatabaseEntity findByBarberIdAndDate(Integer barberId, Timestamp date) {
        return appointmentRepository.findByBarberIdAndDate(barberId, date);
    }

    @Override
    @Transactional
    public void deleteAppointmentsOlderThan10Days() {
        LocalDateTime cutoffLocalDateTime = LocalDateTime.now().minusDays(10);
        Timestamp cutoffTimestamp = Timestamp.valueOf(cutoffLocalDateTime);
        appointmentRepository.deleteAppointmentsOlderThan(cutoffTimestamp);
    }
    
    /**
     * Check if an appointment already exists for the given barber and date
     */
    public void checkAppointmentConflict(Integer barberId, Timestamp date) {
        AppointmentDatabaseEntity existingAppointment = appointmentRepository.findByBarberIdAndDate(barberId, date);
        if (existingAppointment != null) {
            throw AppointmentException.alreadyExists(barberId.toString(), date.toString());
        }
    }
    
} 