package com.example.barbershop.controller;

import com.example.barbershop.entity.AppointmentDatabaseEntity;
import com.example.barbershop.exception.AppointmentException;
import com.example.barbershop.exception.ValidationException;
import com.example.barbershop.exception.ValidationUtils;
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
    public String deleteAppointment(@RequestParam("appointmentDate") Timestamp appointmentDate, 
                                   @RequestParam("barberId") Integer barberId,
                                   RedirectAttributes redirectAttributes) {
        try {
            ValidationUtils.validateBarberId(barberId);
            appointmentService.deleteById(new AppointmentId(appointmentDate, barberId));
            redirectAttributes.addFlashAttribute("success", "Appointment deleted successfully!");
        } catch (AppointmentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
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
            @RequestParam("barberId") Integer barberId,
            RedirectAttributes redirectAttributes) {
        
        try {
            ValidationUtils.validateRequired(name, "name");
            ValidationUtils.validateRequired(phone, "phone");
            ValidationUtils.validateBarberId(barberId);
            ValidationUtils.validatePhone(phone);
            
            AppointmentDatabaseEntity appointmentDatabaseEntity = appointmentService.findByBarberIdAndDate(barberId, originalDate);
            
            if (appointmentDatabaseEntity == null) {
                throw AppointmentException.notFound(originalDate.toString(), barberId.toString());
            }
            
            appointmentService.deleteById(new AppointmentId(originalDate, barberId));
            
            AppointmentDatabaseEntity newAppointmentDatabaseEntity = new AppointmentDatabaseEntity();
            newAppointmentDatabaseEntity.setName(name);
            newAppointmentDatabaseEntity.setPhone(phone);
            newAppointmentDatabaseEntity.setComment(comment);
            newAppointmentDatabaseEntity.setBarberId(barberId);
            
            LocalDateTime newDateTime = ValidationUtils.validateAppointmentDate(newDateStr);
            newAppointmentDatabaseEntity.setDate(Timestamp.valueOf(newDateTime));
            
            appointmentService.save(newAppointmentDatabaseEntity);
            
            redirectAttributes.addFlashAttribute("success", "Appointment updated successfully!");
        } catch (AppointmentException | ValidationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/dashboard";
    }

    @PostMapping("/appointments/booking")
    public String booking(@RequestParam("name") String name,
                          @RequestParam("phone") String phone,
                          @RequestParam("date") String dateTimeStr,
                          @RequestParam("message") String message,
                          @RequestParam("barber") Integer barberId,
                          RedirectAttributes redirectAttributes) {
        try {
            ValidationUtils.validateRequired(name, "name");
            ValidationUtils.validateRequired(phone, "phone");
            ValidationUtils.validateBarberId(barberId);
            ValidationUtils.validatePhone(phone);
            ValidationUtils.validateLength(name, "name", 2, 50);
            
            LocalDateTime dateTime = ValidationUtils.validateAppointmentDate(dateTimeStr);
            
            appointmentService.checkAppointmentConflict(barberId, Timestamp.valueOf(dateTime));
            
            AppointmentDatabaseEntity appointmentDatabaseEntity = new AppointmentDatabaseEntity();
            appointmentDatabaseEntity.setName(name);
            appointmentDatabaseEntity.setPhone(phone);
            appointmentDatabaseEntity.setDate(Timestamp.valueOf(dateTime));
            appointmentDatabaseEntity.setComment(message);
            appointmentDatabaseEntity.setBarberId(barberId);
            
            appointmentService.save(appointmentDatabaseEntity);
            
            redirectAttributes.addFlashAttribute("success", "Appointment booked successfully!");
        } catch (AppointmentException | ValidationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/";
    }
}
