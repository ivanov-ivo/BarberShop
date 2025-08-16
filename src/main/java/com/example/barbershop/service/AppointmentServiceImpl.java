package com.example.barbershop.service;

import com.example.barbershop.dao.AppointmentRepository;
import com.example.barbershop.entity.Appointment;
import org.springframework.stereotype.Service;
import com.example.barbershop.entity.BarberId;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }
    
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }
    
    public Appointment findById(BarberId id) {
        return appointmentRepository.findById(id).orElse(null);
    }
    
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
    
    public void deleteById(BarberId id) {
        appointmentRepository.deleteById(id);
    }

    public List<Appointment> findByBarberId(Long barberId) {
        return appointmentRepository.findByBarberId(barberId);
    }

    public Appointment findByBarberIdAndDate(Long barberId, Timestamp date) {
        return appointmentRepository.findByBarberIdAndDate(barberId, date);
    }

    /**
     * Deletes all appointments that are 10 days old or older.
     */
    @Transactional
    public void deleteAppointmentsOlderThan10Days() {
        LocalDateTime cutoffLocalDateTime = LocalDateTime.now().minusDays(10);
        Timestamp cutoffTimestamp = Timestamp.valueOf(cutoffLocalDateTime);
        appointmentRepository.deleteAppointmentsOlderThan(cutoffTimestamp);
    }
    
} 