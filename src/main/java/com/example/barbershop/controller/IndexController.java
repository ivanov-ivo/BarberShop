package com.example.barbershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import com.example.barbershop.service.BarberServiceImpl;
import com.example.barbershop.entity.BarberDatabaseEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;


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
}
