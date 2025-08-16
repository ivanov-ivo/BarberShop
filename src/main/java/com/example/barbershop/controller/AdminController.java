package com.example.barbershop.controller;

import com.example.barbershop.entity.AppointmentDatabaseEntity;
import com.example.barbershop.entity.BarberDatabaseEntity;
import com.example.barbershop.entity.UserDatabaseEntity;
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

@Controller
public class AdminController {

    private UserServiceImpl userServiceImpl;
    private BarberServiceImpl barberServiceImpl;
    private AppointmentServiceImpl appointmentService;
    private FileUploadService fileUploadService;
    private PasswordEncoder passwordEncoder;

    @Value("${branches}")
    private List<String> branches;

    public AdminController(UserServiceImpl userServiceImpl, BarberServiceImpl barberServiceImpl, AppointmentServiceImpl appointmentService, FileUploadService fileUploadService, PasswordEncoder passwordEncoder) {
        this.userServiceImpl = userServiceImpl;
        this.barberServiceImpl = barberServiceImpl;
        this.appointmentService = appointmentService;
        this.fileUploadService = fileUploadService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/admin")
    public String admin(Model model, Authentication authentication) {
        UserDatabaseEntity userDatabaseEntity = userServiceImpl.findById(authentication.getName());
        BarberDatabaseEntity barberDatabaseEntity = barberServiceImpl.findById(userDatabaseEntity.getBarberId());
        List<BarberDatabaseEntity> barberDatabaseEntities = barberServiceImpl.findAll();
        List<AppointmentDatabaseEntity> appointmentDatabaseEntities = appointmentService.findByBarberId(barberDatabaseEntity.getId());
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
                          @RequestParam("barberRole") String role) {
        try {
            String photoPath = fileUploadService.uploadFile(photo);
            BarberDatabaseEntity barberDatabaseEntity = new BarberDatabaseEntity();
            barberDatabaseEntity.setName(name);
            barberDatabaseEntity.setPhoto(photoPath);
            barberDatabaseEntity.setBranch(branch);
            barberDatabaseEntity.setInformation(information);
            
            // Save barberDatabaseEntity first to get the ID
            barberDatabaseEntity = barberServiceImpl.save(barberDatabaseEntity);
            
            // Now create and save userDatabaseEntity with the barberDatabaseEntity's ID
            UserDatabaseEntity userDatabaseEntity = new UserDatabaseEntity();
            userDatabaseEntity.setUsername(email);
            userDatabaseEntity.setPassword(passwordEncoder.encode(password));
            userDatabaseEntity.setEnabled(true);
            userDatabaseEntity.setRole(role);
            userDatabaseEntity.setBarberId(barberDatabaseEntity.getId());
            userServiceImpl.save(userDatabaseEntity);
            
            return "redirect:/admin";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/admin?error=true";
        }
    }

    @PostMapping("/admin/editBarber/{barberId}")
    public String editBarber(@PathVariable("barberId") Long id,
                           @RequestParam("barberName") String name,
                           @RequestParam(value = "barberPhoto", required = false) MultipartFile photo,
                           @RequestParam("barberBranch") String branch,
                           @RequestParam("barberInformation") String information,
                           @RequestParam(value = "barberEmail", required = false) String email,
                           @RequestParam(value = "barberPassword", required = false) String password,
                           @RequestParam(value = "barberRole", required = false) String role) {
        try {
            BarberDatabaseEntity barberDatabaseEntity = barberServiceImpl.findById(id.longValue());
            UserDatabaseEntity userDatabaseEntity = userServiceImpl.findByBarberId(id.longValue());
            
            if (barberDatabaseEntity != null && userDatabaseEntity != null) {
                barberDatabaseEntity.setName(name);
                barberDatabaseEntity.setBranch(branch);
                barberDatabaseEntity.setInformation(information);
                
                // Update email and password if provided
                if (email != null && !email.isEmpty()) {
                    userDatabaseEntity.setUsername(email);
                }
                if (password != null && !password.isEmpty()) {
                    userDatabaseEntity.setPassword(passwordEncoder.encode(password));
                }
                // Update role if provided
                
                userDatabaseEntity.setEnabled(true);
                
                // Only update photo if a new one is provided
                if (photo != null && !photo.isEmpty()) {
                    String photoPath = fileUploadService.uploadFile(photo);
                    barberDatabaseEntity.setPhoto(photoPath);
                }
                
                barberServiceImpl.save(barberDatabaseEntity);
                userDatabaseEntity.setRole(role);
                userServiceImpl.save(userDatabaseEntity);
            }
            return "redirect:/admin";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/admin?error=true";
        }
    }

    @PostMapping("/admin/deleteBarber")
    public String deleteBarber(@RequestParam("id") Long id) {
        try {
            // Get the barberDatabaseEntity first
            BarberDatabaseEntity barberDatabaseEntity = barberServiceImpl.findById(id);
            if (barberDatabaseEntity != null) {
                // Delete associated userDatabaseEntity first
                UserDatabaseEntity userDatabaseEntity = userServiceImpl.findByBarberId(id.longValue());
                if (userDatabaseEntity != null) {
                    userServiceImpl.deleteById(userDatabaseEntity.getUsername());
                }
                
                // Delete photo file if exists
                if (barberDatabaseEntity.getPhoto() != null) {
                    try {
                        fileUploadService.deleteFile(barberDatabaseEntity.getPhoto().substring(14));
                    } catch (IOException e) {
                        // Log error but continue with deletion
                        e.printStackTrace();
                    }
                }
                
                // Finally delete the barberDatabaseEntity
                barberServiceImpl.deleteById(id.longValue());
            }
            return "redirect:/admin";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin?error=true";
        }
    }

    @GetMapping("/admin/editBarber/{id}")
    public String showEditBarberForm(@PathVariable("id") Long id, Model model) {
        BarberDatabaseEntity barberDatabaseEntity = barberServiceImpl.findById(id.longValue());
        if (barberDatabaseEntity == null) {
            return "redirect:/admin";
        }
        
        model.addAttribute("barber", barberDatabaseEntity);
        model.addAttribute("branches", branches);
        return "admin";
    }

    @GetMapping("/admin/getBarber/{id}")
    @ResponseBody
    public ResponseEntity<?> getBarber(@PathVariable Long id) {
        try {
            System.out.println("Fetching barberDatabaseEntity with ID: " + id);
            BarberDatabaseEntity barberDatabaseEntity = barberServiceImpl.findById(id.longValue());
            if (barberDatabaseEntity == null) {
                System.out.println("BarberDatabaseEntity not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }
            System.out.println("Found barberDatabaseEntity: " + barberDatabaseEntity.getName());
            
            UserDatabaseEntity userDatabaseEntity = userServiceImpl.findByBarberId(id.longValue());
            if (userDatabaseEntity == null) {
                System.out.println("UserDatabaseEntity not found for barberDatabaseEntity ID: " + id);
                return ResponseEntity.notFound().build();
            }
            System.out.println("Found userDatabaseEntity: " + userDatabaseEntity.getUsername());
            
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
            
            System.out.println("Returning response: " + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in getBarber: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error fetching barber details: " + e.getMessage());
        }
    }

    @GetMapping("/admin/appointments/{id}")
    @ResponseBody
    public ResponseEntity<?> getBarberAppointments(@PathVariable Long id) {
        try {
            List<AppointmentDatabaseEntity> appointmentDatabaseEntities = appointmentService.findByBarberId(id);
            System.out.println("Fetching appointmentDatabaseEntities for barber " + id + ": " + appointmentDatabaseEntities.size() + " found");
            return ResponseEntity.ok(appointmentDatabaseEntities);
        } catch (Exception e) {
            System.err.println("Error fetching appointments for barber " + id + ": " + e.getMessage());
            return ResponseEntity.badRequest().body("Error fetching appointments");
        }
    }

    @PostMapping("/admin/appointments/{id}/delete")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id, @RequestParam Timestamp date) {
        try {
            appointmentService.deleteById(new AppointmentId(date, id));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error deleting appointment");
        }
    }

}