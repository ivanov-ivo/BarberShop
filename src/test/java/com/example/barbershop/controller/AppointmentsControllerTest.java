package com.example.barbershop.controller;

import com.example.barbershop.entity.AppointmentDatabaseEntity;
import com.example.barbershop.service.AppointmentServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AppointmentsControllerTest {

    @Mock
    private AppointmentServiceInterface appointmentService;

    @InjectMocks
    private AppointmentsController appointmentsController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentsController).build();
    }

    @Test
    void testGetAllAppointments() throws Exception {
        // Given
        List<AppointmentDatabaseEntity> appointments = Arrays.asList(
            createTestAppointment(1, "John Doe", "Haircut", "1234567890"),
            createTestAppointment(2, "Jane Smith", "Beard Trim", "1234567890")
        );
        
        when(appointmentService.findAll()).thenReturn(appointments);

        // When & Then
        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments"))
                .andExpect(model().attributeExists("appointments"));
    }

    @Test
    void testGetAppointmentsByBarber() throws Exception {
        // Given
        Integer barberId = 1;
        List<AppointmentDatabaseEntity> appointments = Arrays.asList(
            createTestAppointment(1, "John Doe", "Haircut", "1234567890")
        );
        
        when(appointmentService.findByBarberId(barberId)).thenReturn(appointments);

        // When & Then
        mockMvc.perform(get("/appointments/barber/{barberId}", barberId))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments"))
                .andExpect(model().attributeExists("appointments"));
    }

    private AppointmentDatabaseEntity createTestAppointment(Integer id, String customerName, String comment, String phone) {
        AppointmentDatabaseEntity appointment = new AppointmentDatabaseEntity();
        appointment.setDate(Timestamp.valueOf(LocalDateTime.now()));
        appointment.setBarberId(id);
        appointment.setName(customerName);
        appointment.setComment(comment);
        appointment.setPhone(phone);
        return appointment;
    }
}
