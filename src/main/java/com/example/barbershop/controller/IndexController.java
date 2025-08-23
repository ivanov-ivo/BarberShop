package com.example.barbershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import com.example.barbershop.service.BarberServiceImpl;
import com.example.barbershop.entity.BarberDatabaseEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.IOException;


@Controller
public class IndexController {

    private BarberServiceImpl barberServiceImpl;

    @Value("${branches}")
    private List<String> branches;

    public IndexController(BarberServiceImpl barberServiceImpl) {
        this.barberServiceImpl = barberServiceImpl;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<BarberDatabaseEntity> barberDatabaseEntities = barberServiceImpl.findAll();
        model.addAttribute("barbers", barberDatabaseEntities);
        model.addAttribute("branches", branches);
        return "index";
    }
    
    @GetMapping("/favicon.ico")
    @ResponseBody
    public ResponseEntity<byte[]> favicon() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/images/templatemo-barber-logo.png");
        byte[] imageBytes = resource.getInputStream().readAllBytes();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }
}
