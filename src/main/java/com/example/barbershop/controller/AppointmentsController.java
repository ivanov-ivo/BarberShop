package com.example.barbershop.controller;

import com.example.barbershop.entity.AppointmentDatabaseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

import com.example.barbershop.service.AppointmentServiceImpl;
import com.example.barbershop.entity.AppointmentId;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AppointmentsController {
    
    private final AppointmentServiceImpl appointmentService;

    public AppointmentsController(AppointmentServiceImpl appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/appointments/delete")
    public String deleteAppointment(@RequestParam("appointmentDate") Timestamp appointmentDate, @RequestParam("barberId") Long barberId) {
        try {
            appointmentService.deleteById(new AppointmentId(appointmentDate, barberId));
        } catch (Exception e) {
            // Log the error
            System.err.println("Error deleting appointment: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/appointments/edit")
    public String editAppointment(
            @RequestParam("originalDate") Timestamp originalDate,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("newDate") String newDateStr,
            @RequestParam("comment") String comment,
            @RequestParam("barberId") Long barberId) {
        
        try {
            // Get the original appointmentDatabaseEntity
            AppointmentDatabaseEntity appointmentDatabaseEntity = appointmentService.findByBarberIdAndDate(barberId, originalDate);
            
            if (appointmentDatabaseEntity != null) {
                // Delete the old appointmentDatabaseEntity
                appointmentService.deleteById(new AppointmentId(originalDate, barberId));
                
                // Create a new appointmentDatabaseEntity with updated details
                AppointmentDatabaseEntity newAppointmentDatabaseEntity = new AppointmentDatabaseEntity();
                newAppointmentDatabaseEntity.setName(name);
                newAppointmentDatabaseEntity.setPhone(phone);
                newAppointmentDatabaseEntity.setComment(comment);
                newAppointmentDatabaseEntity.setBarberId(barberId);
                
                // Parse the date using Flatpickr's format (YYYY-MM-DD HH:mm)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime newDateTime = LocalDateTime.parse(newDateStr, formatter);
                newAppointmentDatabaseEntity.setDate(Timestamp.valueOf(newDateTime));
                
                // Save the new appointmentDatabaseEntity
                appointmentService.save(newAppointmentDatabaseEntity);
            }
        } catch (Exception e) {
            // Log the error
            System.err.println("Error updating appointment: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }

    @PostMapping("/appointments/booking")
    public String booking(@RequestParam("name") String name,
                          @RequestParam("phone") String phone,
                          @RequestParam("date") String dateTimeStr,
                          @RequestParam("message") String message,
                          @RequestParam("barber") Long barberId,
                          RedirectAttributes redirectAttributes) {
        try {
            AppointmentDatabaseEntity appointmentDatabaseEntity = new AppointmentDatabaseEntity();
            appointmentDatabaseEntity.setName(name);
            appointmentDatabaseEntity.setPhone(phone);
            
            // Parse the date using Flatpickr's format (YYYY-MM-DD HH:mm)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);
            
            // Set the appointmentDatabaseEntity date
            appointmentDatabaseEntity.setDate(Timestamp.valueOf(dateTime));
            appointmentDatabaseEntity.setComment(message);
            appointmentDatabaseEntity.setBarberId(barberId);
            
            // Save the appointmentDatabaseEntity
            appointmentService.save(appointmentDatabaseEntity);
            
            redirectAttributes.addFlashAttribute("success", "Appointment booked successfully!");
            return "redirect:/";
        } catch (Exception e) {
            System.err.println("Error creating appointment: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to book appointment. Please try again.");
            return "redirect:/";
        }
    }
}
