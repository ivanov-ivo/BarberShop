package com.example.barbershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import com.example.barbershop.service.UserService;
import com.example.barbershop.service.BarberService;
import com.example.barbershop.entity.User;
import com.example.barbershop.entity.Barber;
import org.springframework.ui.Model;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import com.example.barbershop.entity.Appointment;
import com.example.barbershop.service.AppointmentService;

@Controller
public class DashboardController {

    private UserService userService;
    private BarberService barberService;
    private AppointmentService appointmentService;

    public DashboardController(UserService userService, BarberService barberService, AppointmentService appointmentService) {
        this.userService = userService;
        this.barberService = barberService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findById(username);
        Barber barber = barberService.findById(user.getBarberId());
        model.addAttribute("barber", barber);
        model.addAttribute("user", user);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.toLocalDate().plusDays(1).atStartOfDay();
        
        List<Appointment> appointments = appointmentService.findByBarberId(barber.getId());
        List<Appointment> todayAppointments = appointments.stream()
            .filter(appointment -> {
                LocalDateTime appointmentDateTime = appointment.getDate().toLocalDateTime();
                return appointmentDateTime.isAfter(now) && appointmentDateTime.isBefore(endOfDay);
            })
            .collect(Collectors.toList());
        model.addAttribute("todayAppointments", todayAppointments);
        
        List<Appointment> upcomingAppointments = appointments.stream()
            .filter(appointment -> appointment.getDate().toLocalDateTime().isAfter(endOfDay))
            .limit(10)
            .collect(Collectors.toList());
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        
        List<Appointment> pastAppointments = appointments.stream()
            .filter(appointment -> appointment.getDate().toLocalDateTime().isBefore(now))
            .limit(10)
            .collect(Collectors.toList());
        Collections.reverse(pastAppointments);
        model.addAttribute("pastAppointments", pastAppointments);
        
        return "dashboard";
    }
}
