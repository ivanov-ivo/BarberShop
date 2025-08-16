package com.example.barbershop.controller;

import com.example.barbershop.entity.UserDatabaseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import com.example.barbershop.service.UserServiceImpl;
import com.example.barbershop.service.BarberServiceImpl;
import com.example.barbershop.entity.BarberDatabaseEntity;
import org.springframework.ui.Model;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import com.example.barbershop.entity.AppointmentDatabaseEntity;
import com.example.barbershop.service.AppointmentServiceImpl;

@Controller
public class DashboardController {

    private UserServiceImpl userServiceImpl;
    private BarberServiceImpl barberServiceImpl;
    private AppointmentServiceImpl appointmentService;

    public DashboardController(UserServiceImpl userServiceImpl, BarberServiceImpl barberServiceImpl, AppointmentServiceImpl appointmentService) {
        this.userServiceImpl = userServiceImpl;
        this.barberServiceImpl = barberServiceImpl;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        UserDatabaseEntity userDatabaseEntity = userServiceImpl.findById(username);
        BarberDatabaseEntity barberDatabaseEntity = barberServiceImpl.findById(userDatabaseEntity.getBarberId());
        model.addAttribute("barber", barberDatabaseEntity);
        model.addAttribute("user", userDatabaseEntity);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.toLocalDate().plusDays(1).atStartOfDay();
        
        List<AppointmentDatabaseEntity> appointmentDatabaseEntities = appointmentService.findByBarberId(barberDatabaseEntity.getId());
        List<AppointmentDatabaseEntity> todayAppointmentDatabaseEntities = appointmentDatabaseEntities.stream()
            .filter(appointment -> {
                LocalDateTime appointmentDateTime = appointment.getDate().toLocalDateTime();
                return appointmentDateTime.isAfter(now) && appointmentDateTime.isBefore(endOfDay);
            })
            .collect(Collectors.toList());
        model.addAttribute("todayAppointments", todayAppointmentDatabaseEntities);
        
        List<AppointmentDatabaseEntity> upcomingAppointmentDatabaseEntities = appointmentDatabaseEntities.stream()
            .filter(appointment -> appointment.getDate().toLocalDateTime().isAfter(endOfDay))
            .limit(10)
            .collect(Collectors.toList());
        model.addAttribute("upcomingAppointments", upcomingAppointmentDatabaseEntities);
        
        List<AppointmentDatabaseEntity> pastAppointmentDatabaseEntities = appointmentDatabaseEntities.stream()
            .filter(appointment -> appointment.getDate().toLocalDateTime().isBefore(now))
            .limit(10)
            .collect(Collectors.toList());
        Collections.reverse(pastAppointmentDatabaseEntities);
        model.addAttribute("pastAppointments", pastAppointmentDatabaseEntities);
        
        return "dashboard";
    }
}
