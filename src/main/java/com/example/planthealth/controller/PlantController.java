package com.example.planthealth.controller;

import com.example.planthealth.model.Plant;
import com.example.planthealth.repository.PlantRepository;
import com.example.planthealth.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Controller
public class PlantController {

    @Autowired
    private PlantService service;

    @Autowired
    private PlantRepository repository;

    @GetMapping("/")
    public String home(Model model) {
        List<Plant> plants = repository.findAll();
        model.addAttribute("plants", plants);
        return "index";
    }

    @PostMapping("/api/plant/analyze")
    public String analyzePlant(@RequestParam String name,
            @RequestParam String type,
            @RequestParam MultipartFile image) {
        try {
            String uploadDir = "src/main/resources/static/assets/";
            File file = new File(uploadDir + image.getOriginalFilename());
            image.transferTo(file);

            Plant plant = new Plant();
            plant.setName(name);
            plant.setType(type);
            plant.setImagePath("/assets/" + image.getOriginalFilename());

            service.analyzePlant(plant);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }
}
