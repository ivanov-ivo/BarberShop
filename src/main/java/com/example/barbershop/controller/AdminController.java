package com.example.barbershop.controller;

import com.example.barbershop.entity.AppointmentDatabaseEntity;
import com.example.barbershop.entity.BarberDatabaseEntity;
import com.example.barbershop.entity.UserDatabaseEntity;
import com.example.barbershop.exception.BarberException;
import com.example.barbershop.exception.UserException;
import com.example.barbershop.exception.FileUploadException;
import com.example.barbershop.exception.ValidationException;
import com.example.barbershop.exception.ValidationUtils;
import com.example.barbershop.exception.AppointmentException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import com.example.barbershop.service.UserServiceImpl;
import com.example.barbershop.service.BarberServiceImpl;
import com.example.barbershop.service.AppointmentServiceImpl;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import com.example.barbershop.service.FileUploadService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import com.example.barbershop.entity.AppointmentId;
import java.sql.Timestamp;
import java.util.Collections;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private UserServiceImpl userServiceImpl;
    private BarberServiceImpl barberServiceImpl;
    private AppointmentServiceImpl appointmentServiceImpl;
    private FileUploadService fileUploadService;
    private PasswordEncoder passwordEncoder;

    @Value("${branches}")
    private List<String> branches;

    public AdminController(UserServiceImpl userServiceImpl, BarberServiceImpl barberServiceImpl, AppointmentServiceImpl appointmentService, FileUploadService fileUploadService, PasswordEncoder passwordEncoder) {
        this.userServiceImpl = userServiceImpl;
        this.barberServiceImpl = barberServiceImpl;
        this.appointmentServiceImpl = appointmentService;
        this.fileUploadService = fileUploadService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/admin")
    public String admin(Model model, Authentication authentication) {
        UserDatabaseEntity userDatabaseEntity = userServiceImpl.findById(authentication.getName());
        BarberDatabaseEntity barberDatabaseEntity = barberServiceImpl.findById(userDatabaseEntity.getBarberId());
        List<BarberDatabaseEntity> barberDatabaseEntities = barberServiceImpl.findAll();
        List<AppointmentDatabaseEntity> appointmentDatabaseEntities = appointmentServiceImpl.findByBarberId(barberDatabaseEntity.getId());
        Collections.reverse(appointmentDatabaseEntities);
        
        model.addAttribute("user", userDatabaseEntity);
        model.addAttribute("barber", barberDatabaseEntity);
        model.addAttribute("barbers", barberDatabaseEntities);
        model.addAttribute("branches", branches);
        model.addAttribute("appointments", appointmentDatabaseEntities);
        return "admin";
    }

    @PostMapping("/admin/addBarber")
    public String addBarber(@RequestParam("barberName") String name,
                          @RequestParam("barberPhoto") MultipartFile photo,
                          @RequestParam("barberBranch") String branch,
                          @RequestParam("barberInformation") String information,
                          @RequestParam("barberEmail") String email,
                          @RequestParam("barberPassword") String password,
                          @RequestParam("barberRole") String role,
                          RedirectAttributes redirectAttributes) {
        try {
            ValidationUtils.validateRequired(name, "barberName");
            ValidationUtils.validateRequired(email, "barberEmail");
            ValidationUtils.validateRequired(password, "barberPassword");
            ValidationUtils.validateRequired(branch, "barberBranch");
            ValidationUtils.validateEmail(email);
            ValidationUtils.validateLength(name, "barberName", 2, 50);
            ValidationUtils.validateLength(password, "barberPassword", 6, 100);
            
            String photoPath = fileUploadService.uploadFile(photo);
            
            BarberDatabaseEntity barberDatabaseEntity = new BarberDatabaseEntity();
            barberDatabaseEntity.setName(name);
            barberDatabaseEntity.setPhoto(photoPath);
            barberDatabaseEntity.setBranch(branch);
            barberDatabaseEntity.setInformation(information);
            
            barberDatabaseEntity = barberServiceImpl.save(barberDatabaseEntity);
            
            UserDatabaseEntity userDatabaseEntity = new UserDatabaseEntity();
            userDatabaseEntity.setUsername(email);
            userDatabaseEntity.setPassword(passwordEncoder.encode(password));
            userDatabaseEntity.setEnabled(true);
            userDatabaseEntity.setRole(role);
            userDatabaseEntity.setBarberId(barberDatabaseEntity.getId());
            userServiceImpl.save(userDatabaseEntity);
            
            redirectAttributes.addFlashAttribute("success", "Barber added successfully!");
            return "redirect:/admin";
        } catch (BarberException | UserException | FileUploadException | ValidationException | IOException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin";
        }
    }

    @PostMapping("/admin/editBarber/{barberId}")
    public String editBarber(@PathVariable("barberId") Integer id,
                           @RequestParam("barberName") String name,
                           @RequestParam(value = "barberPhoto", required = false) MultipartFile photo,
                           @RequestParam("barberBranch") String branch,
                           @RequestParam("barberInformation") String information,
                           @RequestParam(value = "barberEmail", required = false) String email,
                           @RequestParam(value = "barberPassword", required = false) String password,
                           @RequestParam(value = "barberRole", required = false) String role,
                           RedirectAttributes redirectAttributes) {
        try {
            ValidationUtils.validateRequired(name, "barberName");
            ValidationUtils.validateRequired(branch, "barberBranch");
            ValidationUtils.validateLength(name, "barberName", 2, 50);
            
            if (email != null && !email.isEmpty()) {
                ValidationUtils.validateEmail(email);
            }
            if (password != null && !password.isEmpty()) {
                ValidationUtils.validateLength(password, "barberPassword", 6, 100);
            }
            
            BarberDatabaseEntity barberDatabaseEntity = barberServiceImpl.findById(id);
            UserDatabaseEntity userDatabaseEntity = userServiceImpl.findByBarberId(id);
            
            barberDatabaseEntity.setName(name);
            barberDatabaseEntity.setBranch(branch);
            barberDatabaseEntity.setInformation(information);
            
            if (email != null && !email.isEmpty()) {
                userDatabaseEntity.setUsername(email);
            }
            if (password != null && !password.isEmpty()) {
                userDatabaseEntity.setPassword(passwordEncoder.encode(password));
            }
            userDatabaseEntity.setEnabled(true);
            userDatabaseEntity.setRole(role);
            
            if (photo != null && !photo.isEmpty()) {
                String photoPath = fileUploadService.uploadFile(photo);
                barberDatabaseEntity.setPhoto(photoPath);
            }
            
            barberServiceImpl.save(barberDatabaseEntity);
            userServiceImpl.save(userDatabaseEntity);
            
            redirectAttributes.addFlashAttribute("success", "Barber updated successfully!");
            return "redirect:/admin";
        } catch (BarberException | UserException | FileUploadException | ValidationException | IOException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin";
        }
    }

    @PostMapping("/admin/deleteBarber")
    public String deleteBarber(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            ValidationUtils.validateBarberId(id);
            
            BarberDatabaseEntity barberDatabaseEntity = barberServiceImpl.findById(id);
            
            UserDatabaseEntity userDatabaseEntity = userServiceImpl.findByBarberId(id);
            if (userDatabaseEntity != null) {
                userServiceImpl.deleteById(userDatabaseEntity.getUsername());
            }
            
            if (barberDatabaseEntity.getPhoto() != null) {
                try {
                    fileUploadService.deleteFile(barberDatabaseEntity.getPhoto().substring(14));
                } catch (IOException e) {
                    System.err.println("Failed to delete photo file: " + e.getMessage());
                }
            }
            
            barberServiceImpl.deleteById(id);
            
            redirectAttributes.addFlashAttribute("success", "Barber deleted successfully!");
            return "redirect:/admin";
        } catch (BarberException | UserException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin";
        }
    }

    @GetMapping("/admin/editBarber/{id}")
    public String showEditBarberForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ValidationUtils.validateBarberId(id);
            BarberDatabaseEntity barberDatabaseEntity = barberServiceImpl.findById(id);
            model.addAttribute("barber", barberDatabaseEntity);
            model.addAttribute("branches", branches);
            return "admin";
        } catch (BarberException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin";
        }
    }

    @GetMapping("/admin/getBarber/{id}")
    @ResponseBody
    public ResponseEntity<?> getBarber(@PathVariable Integer id) {
        try {
            ValidationUtils.validateBarberId(id);
            
            BarberDatabaseEntity barberDatabaseEntity = barberServiceImpl.findById(id);
            UserDatabaseEntity userDatabaseEntity = userServiceImpl.findByBarberId(id);
            String role = userServiceImpl.getUserRole(userDatabaseEntity.getUsername());
            
            var response = new java.util.HashMap<String, Object>();
            response.put("id", barberDatabaseEntity.getId());
            response.put("name", barberDatabaseEntity.getName());
            response.put("photo", barberDatabaseEntity.getPhoto());
            response.put("branch", barberDatabaseEntity.getBranch());
            response.put("information", barberDatabaseEntity.getInformation());
            response.put("email", userDatabaseEntity.getUsername());
            response.put("password", userDatabaseEntity.getPassword());
            response.put("role", role);
            
            return ResponseEntity.ok(response);
        } catch (BarberException | UserException e) {
            return ResponseEntity.badRequest().body("Error fetching barber details: " + e.getMessage());
        }
    }

    @GetMapping("/admin/appointments/{id}")
    @ResponseBody
    public ResponseEntity<?> getBarberAppointments(@PathVariable Integer id) {
        try {
            ValidationUtils.validateBarberId(id);
            List<AppointmentDatabaseEntity> appointmentDatabaseEntities = appointmentServiceImpl.findByBarberId(id);
            return ResponseEntity.ok(appointmentDatabaseEntities);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching appointments: " + e.getMessage());
        }
    }

    @PostMapping("/admin/appointments/{id}/delete")
    public ResponseEntity<?> deleteAppointment(@PathVariable Integer id, @RequestParam Timestamp date) {
        try {
            ValidationUtils.validateBarberId(id);
            appointmentServiceImpl.deleteById(new AppointmentId(date, id));
            return ResponseEntity.ok().build();
        } catch (AppointmentException e) {
            return ResponseEntity.badRequest().body("Error deleting appointment: " + e.getMessage());
        }
    }

}