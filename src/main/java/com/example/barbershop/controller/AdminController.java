package com.example.barbershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import com.example.barbershop.service.UserService;
import com.example.barbershop.service.BarberService;
import com.example.barbershop.service.AppointmentService;
import com.example.barbershop.entity.User;
import com.example.barbershop.entity.Barber;
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
import com.example.barbershop.entity.Appointment;
import com.example.barbershop.entity.BarberId;
import java.sql.Timestamp;
import java.util.Collections;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class AdminController {

    private UserService userService;
    private BarberService barberService;
    private AppointmentService appointmentService;
    private FileUploadService fileUploadService;
    private PasswordEncoder passwordEncoder;

    @Value("${branches}")
    private List<String> branches;

    public AdminController(UserService userService, BarberService barberService, AppointmentService appointmentService, FileUploadService fileUploadService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.barberService = barberService;
        this.appointmentService = appointmentService;
        this.fileUploadService = fileUploadService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/admin")
    public String admin(Model model, Authentication authentication) {
        User user = userService.findById(authentication.getName());
        Barber barber = barberService.findById(user.getBarberId());
        List<Barber> barbers = barberService.findAll();
        List<Appointment> appointments = appointmentService.findByBarberId(barber.getId());
        Collections.reverse(appointments);
        
        model.addAttribute("user", user);
        model.addAttribute("barber", barber);
        model.addAttribute("barbers", barbers);
        model.addAttribute("branches", branches);
        model.addAttribute("appointments", appointments);
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
            Barber barber = new Barber();
            barber.setName(name);
            barber.setPhoto(photoPath);
            barber.setBranch(branch);
            barber.setInformation(information);
            
            // Save barber first to get the ID
            barber = barberService.save(barber);
            
            // Now create and save user with the barber's ID
            User user = new User();
            user.setUsername(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setEnabled(true);
            user.setRole(role);
            user.setBarberId(barber.getId());
            userService.save(user);
            
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
            Barber barber = barberService.findById(id.longValue());
            User user = userService.findByBarberId(id.longValue());
            
            if (barber != null && user != null) {
                barber.setName(name);
                barber.setBranch(branch);
                barber.setInformation(information);
                
                // Update email and password if provided
                if (email != null && !email.isEmpty()) {
                    user.setUsername(email);
                }
                if (password != null && !password.isEmpty()) {
                    user.setPassword(passwordEncoder.encode(password));
                }
                // Update role if provided
                
                user.setEnabled(true);
                
                // Only update photo if a new one is provided
                if (photo != null && !photo.isEmpty()) {
                    String photoPath = fileUploadService.uploadFile(photo);
                    barber.setPhoto(photoPath);
                }
                
                barberService.save(barber);
                user.setRole(role);
                userService.save(user);
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
            // Get the barber first
            Barber barber = barberService.findById(id);
            if (barber != null) {
                // Delete associated user first
                User user = userService.findByBarberId(id.longValue());
                if (user != null) {
                    userService.deleteById(user.getUsername());
                }
                
                // Delete photo file if exists
                if (barber.getPhoto() != null) {
                    try {
                        fileUploadService.deleteFile(barber.getPhoto().substring(14));
                    } catch (IOException e) {
                        // Log error but continue with deletion
                        e.printStackTrace();
                    }
                }
                
                // Finally delete the barber
                barberService.deleteById(id.longValue());
            }
            return "redirect:/admin";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin?error=true";
        }
    }

    @GetMapping("/admin/editBarber/{id}")
    public String showEditBarberForm(@PathVariable("id") Long id, Model model) {
        Barber barber = barberService.findById(id.longValue());
        if (barber == null) {
            return "redirect:/admin";
        }
        
        model.addAttribute("barber", barber);
        model.addAttribute("branches", branches);
        return "admin";
    }

    @GetMapping("/admin/getBarber/{id}")
    @ResponseBody
    public ResponseEntity<?> getBarber(@PathVariable Long id) {
        try {
            System.out.println("Fetching barber with ID: " + id);
            Barber barber = barberService.findById(id.longValue());
            if (barber == null) {
                System.out.println("Barber not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }
            System.out.println("Found barber: " + barber.getName());
            
            User user = userService.findByBarberId(id.longValue());
            if (user == null) {
                System.out.println("User not found for barber ID: " + id);
                return ResponseEntity.notFound().build();
            }
            System.out.println("Found user: " + user.getUsername());
            
            String role = userService.getUserRole(user.getUsername());
            
            var response = new java.util.HashMap<String, Object>();
            response.put("id", barber.getId());
            response.put("name", barber.getName());
            response.put("photo", barber.getPhoto());
            response.put("branch", barber.getBranch());
            response.put("information", barber.getInformation());
            response.put("email", user.getUsername());
            response.put("password", user.getPassword());
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
            List<Appointment> appointments = appointmentService.findByBarberId(id);
            System.out.println("Fetching appointments for barber " + id + ": " + appointments.size() + " found");
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            System.err.println("Error fetching appointments for barber " + id + ": " + e.getMessage());
            return ResponseEntity.badRequest().body("Error fetching appointments");
        }
    }

    @PostMapping("/admin/appointments/{id}/delete")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id, @RequestParam Timestamp date) {
        try {
            appointmentService.deleteById(new BarberId(date, id));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error deleting appointment");
        }
    }

}