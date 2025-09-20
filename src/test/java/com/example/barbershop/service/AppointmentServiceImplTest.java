package com.example.barbershop.service;

import com.example.barbershop.dao.AppointmentRepository;
import com.example.barbershop.entity.AppointmentDatabaseEntity;
import com.example.barbershop.exception.AppointmentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.barbershop.entity.AppointmentId;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private AppointmentDatabaseEntity testAppointment;

    @BeforeEach
    void setUp() {
        testAppointment = new AppointmentDatabaseEntity();
        testAppointment.setBarberId(1);
        testAppointment.setName("John Doe");
        testAppointment.setPhone("1234567890");
        testAppointment.setComment("Test Comment");
    }

    @Test
    void testGetAllAppointments() {
        // Given
        List<AppointmentDatabaseEntity> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        // When
        List<AppointmentDatabaseEntity> result = appointmentService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(appointmentRepository, times(1)).findAll();
    }

    @Test
    void testGetAppointmentById() {
        // Given
        Integer appointmentId = 1;
        Timestamp date = Timestamp.valueOf(LocalDateTime.now());
        when(appointmentRepository.findById(new AppointmentId(date, appointmentId))).thenReturn(Optional.of(testAppointment));

        // When
        AppointmentDatabaseEntity result = appointmentService.findById(new AppointmentId(date, appointmentId));

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(appointmentRepository, times(1)).findById(new AppointmentId(date, appointmentId));
    }

    @Test
    void testGetAppointmentByIdNotFound() {
        // Given
        Integer appointmentId = 999;
        Timestamp date = Timestamp.valueOf(LocalDateTime.now());
        when(appointmentRepository.findById(new AppointmentId(date, appointmentId))).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AppointmentException.class, () -> {
            appointmentService.findById(new AppointmentId(date, appointmentId));
        });
        verify(appointmentRepository, times(1)).findById(new AppointmentId(date, appointmentId));
    }

    @Test
    void testCreateAppointment() {
        // Given
        when(appointmentRepository.save(any(AppointmentDatabaseEntity.class))).thenReturn(testAppointment);

        // When
        AppointmentDatabaseEntity result = appointmentService.save(testAppointment);

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(appointmentRepository, times(1)).save(testAppointment);
    }

    @Test
    void testUpdateAppointment() {
        // Given
        Integer appointmentId = 1;
        Timestamp date = Timestamp.valueOf(LocalDateTime.now());
        when(appointmentRepository.findById(new AppointmentId(date, appointmentId))).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(AppointmentDatabaseEntity.class))).thenReturn(testAppointment);

        // When
        AppointmentDatabaseEntity result = appointmentService.save(testAppointment);

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(appointmentRepository, times(1)).findById(new AppointmentId(date, appointmentId));
        verify(appointmentRepository, times(1)).save(testAppointment);
    }

    @Test
    void testDeleteAppointment() {
        // Given
        Integer appointmentId = 1;
        Timestamp date = Timestamp.valueOf(LocalDateTime.now());
        when(appointmentRepository.existsById(new AppointmentId(date, appointmentId))).thenReturn(true);

        // When
        appointmentService.deleteById(new AppointmentId(date, appointmentId));

        // Then
        verify(appointmentRepository, times(1)).existsById(new AppointmentId(date, appointmentId));
        verify(appointmentRepository, times(1)).deleteById(new AppointmentId(date, appointmentId));
    }

    @Test
    void testDeleteAppointmentNotFound() {
        // Given
        Integer appointmentId = 999;
        Timestamp date = Timestamp.valueOf(LocalDateTime.now());
        when(appointmentRepository.existsById(new AppointmentId(date, appointmentId))).thenReturn(false);

        // When & Then
        assertThrows(AppointmentException.class, () -> {
            appointmentService.deleteById(new AppointmentId(date, appointmentId));
        });
        verify(appointmentRepository, times(1)).existsById(new AppointmentId(date, appointmentId));
        verify(appointmentRepository, never()).deleteById(new AppointmentId(date, appointmentId));
    }

    @Test
    void testGetAppointmentsByBarber() {
        // Given
        Integer barberId = 1;
        Timestamp date = Timestamp.valueOf(LocalDateTime.now());
        List<AppointmentDatabaseEntity> appointments = Arrays.asList(testAppointment);
        testAppointment.setBarberId(barberId);
        when(appointmentRepository.findById(new AppointmentId(date, barberId))).thenReturn(Optional.of(testAppointment));

        // When
        List<AppointmentDatabaseEntity> result = appointmentService.findByBarberId(barberId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Barber", result.get(0).getBarberId().toString());
        verify(appointmentRepository, times(1)).findByBarberId(barberId);
    }
}
