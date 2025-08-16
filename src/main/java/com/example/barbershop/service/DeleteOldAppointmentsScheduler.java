package com.example.barbershop.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeleteOldAppointmentsScheduler {
    private final AppointmentServiceImpl appointmentService;

    public DeleteOldAppointmentsScheduler(AppointmentServiceImpl appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Scheduled(cron = "0 30 12 * * *")
    public void deleteOldAppointments() {
        appointmentService.deleteAppointmentsOlderThan10Days();
    }
} 