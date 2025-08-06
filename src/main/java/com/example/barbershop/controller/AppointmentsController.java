package com.example.barbershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;
import com.example.barbershop.entity.Appointment;
import com.example.barbershop.service.AppointmentService;
import com.example.barbershop.entity.BarberId;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AppointmentsController {
    
    private final AppointmentService appointmentService;

    public AppointmentsController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/appointments/delete")
    public String deleteAppointment(@RequestParam("appointmentDate") Timestamp appointmentDate, @RequestParam("barberId") Long barberId) {
        try {
            appointmentService.deleteById(new BarberId(appointmentDate, barberId));
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
            // Get the original appointment
            Appointment appointment = appointmentService.findByBarberIdAndDate(barberId, originalDate);
            
            if (appointment != null) {
                // Delete the old appointment
                appointmentService.deleteById(new BarberId(originalDate, barberId));
                
                // Create a new appointment with updated details
                Appointment newAppointment = new Appointment();
                newAppointment.setName(name);
                newAppointment.setPhone(phone);
                newAppointment.setComment(comment);
                newAppointment.setBarberId(barberId);
                
                // Parse the date using Flatpickr's format (YYYY-MM-DD HH:mm)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime newDateTime = LocalDateTime.parse(newDateStr, formatter);
                newAppointment.setDate(Timestamp.valueOf(newDateTime));
                
                // Save the new appointment
                appointmentService.save(newAppointment);
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
            Appointment appointment = new Appointment();
            appointment.setName(name);
            appointment.setPhone(phone);
            
            // Parse the date using Flatpickr's format (YYYY-MM-DD HH:mm)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);
            
            // Set the appointment date
            appointment.setDate(Timestamp.valueOf(dateTime));
            appointment.setComment(message);
            appointment.setBarberId(barberId);
            
            // Save the appointment
            appointmentService.save(appointment);
            
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
