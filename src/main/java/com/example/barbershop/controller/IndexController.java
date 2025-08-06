package com.example.barbershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import com.example.barbershop.service.BarberService;
import com.example.barbershop.entity.Barber;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;


@Controller
public class IndexController {

    private BarberService barberService;

    @Value("${branches}")
    private List<String> branches;

    public IndexController(BarberService barberService) {
        this.barberService = barberService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Barber> barbers = barberService.findAll();
        model.addAttribute("barbers", barbers);
        model.addAttribute("branches", branches);
        return "index";
    }
}
