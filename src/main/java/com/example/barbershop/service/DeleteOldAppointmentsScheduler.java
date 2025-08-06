package com.example.barbershop.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeleteOldAppointmentsScheduler {
    private final AppointmentService appointmentService;

    public DeleteOldAppointmentsScheduler(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // Runs every day at 00:00
    @Scheduled(cron = "0 30 12 * * *")
    public void deleteOldAppointments() {
        appointmentService.deleteAppointmentsOlderThan10Days();
    }
} 