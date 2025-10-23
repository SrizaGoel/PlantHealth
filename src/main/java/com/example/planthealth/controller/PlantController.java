/*package com.example.planthealth.controller;

import com.example.planthealth.model.Plant;
import com.example.planthealth.repository.PlantRepository;
import com.example.planthealth.service.PlantService;
import com.example.planthealth.ar.ARRenderer;
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
            @RequestParam MultipartFile image,
            Model model) {
        try {
            // Save uploaded file to static/assets
            String uploadDir = "src/main/resources/static/assets/";
            File file = new File(uploadDir + image.getOriginalFilename());
            image.transferTo(file);

            Plant plant = new Plant();
            plant.setName(name);
            plant.setType(type);
            plant.setImagePath(file.getAbsolutePath()); // <-- absolute path for Flask

            // Call the ML service
            service.analyzePlant(plant);

            // Generate AR image
            String arImage = ARRenderer.generatePlantHealthImage(plant.getName(), plant.getHealthStatus());
            model.addAttribute("plant", plant);
            model.addAttribute("arImage", arImage);

            return "plant-result";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
    }

    // AR Endpoint
    @GetMapping("/plant/ar/{plantName}/{status}")
    @ResponseBody
    public String getPlantAR(@PathVariable String plantName, @PathVariable String status) {
        String base64Image = ARRenderer.generatePlantHealthImage(plantName, status);
        return "<div style='text-align:center;'><img src='data:image/png;base64," + base64Image + "' /></div>";
    }
}
*/
/*
package com.example.planthealth.controller;

import com.example.planthealth.model.Plant;
import com.example.planthealth.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Controller
public class PlantController {

    @Autowired
    private PlantService service;

    @GetMapping("/")
    public String home(Model model) {
        return "index"; // Your upload form
    }

    @PostMapping("/api/plant/analyze")
    public String analyzePlant(@RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("image") MultipartFile image,
            Model model) {
        try {
            System.out.println("=== Controller: Starting analysis ===");

            // 1️⃣ Use system temp directory - this always exists and is writable
            String tempDir = System.getProperty("java.io.tmpdir");
            String uploadDir = tempDir + "plant_uploads" + File.separator;

            System.out.println("📁 Using temp directory: " + uploadDir);

            // Create directory if it doesn't exist
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
                System.out.println("✅ Created upload directory: " + uploadDir);
            }

            // 2️⃣ Generate unique filename
            String originalFileName = image.getOriginalFilename();
            String fileExtension = originalFileName != null && originalFileName.contains(".")
                    ? originalFileName.substring(originalFileName.lastIndexOf("."))
                    : ".jpg";

            String uniqueFileName = "plant_" + System.currentTimeMillis() + fileExtension;
            File savedFile = new File(uploadDir + uniqueFileName);

            System.out.println("💾 Saving to: " + savedFile.getAbsolutePath());

            // 3️⃣ Save the file
            image.transferTo(savedFile);
            System.out.println("✅ File saved successfully!");

            // 4️⃣ Create Plant object
            Plant plant = new Plant();
            plant.setName(name);
            plant.setType(type);
            plant.setImagePath(savedFile.getAbsolutePath());

            // 5️⃣ Analyze plant
            Plant analyzedPlant = service.analyzePlant(plant);

            // 6️⃣ Pass data to template
            model.addAttribute("plant", analyzedPlant);

            return "plant-result";

        } catch (Exception e) {
            System.err.println("❌ Controller ERROR: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/?error=" + e.getMessage();
        }
    }
}
*/
package com.example.planthealth.controller;

import com.example.planthealth.model.Plant;
import com.example.planthealth.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

@Controller
public class PlantController {

    @Autowired
    private PlantService service;

    @GetMapping("/")
    public String home(Model model) {
        return "index"; // Your upload form
    }

    @PostMapping("/api/plant/analyze")
    public String analyzePlant(@RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("image") MultipartFile image,
            Model model) {
        try {
            System.out.println("=== ULTRA SIMPLE CONTROLLER ===");

            // 1. ONLY DO AI DISEASE DETECTION
            System.out.println("🔍 Calling AI Detection...");
            Map<String, Object> aiResult = service.analyzePlantDisease(image);

            // 2. Create plant with AI results
            Plant plant = new Plant();
            plant.setName(name);
            plant.setType(type);
            plant.setDisease((String) aiResult.get("disease"));
            plant.setConfidence((Double) aiResult.get("confidence"));
            plant.setHealthy((Boolean) aiResult.get("is_healthy"));

            System.out.println("✅ SUCCESS: " + plant.getDisease() + " - " + plant.getConfidence());

            // 3. Pass to template
            model.addAttribute("plant", plant);
            model.addAttribute("disease", plant.getDisease());
            model.addAttribute("confidence", plant.getConfidence());
            model.addAttribute("isHealthy", plant.getHealthy());
            model.addAttribute("topPredictions", aiResult.get("top_predictions"));

            return "plant-result";

        } catch (Exception e) {
            System.err.println("❌ CONTROLLER ERROR: " + e.getMessage());
            model.addAttribute("error", "AI Detection Failed: " + e.getMessage());
            return "index";
        }
    }

    // KEEP YOUR EXISTING METHODS, JUST ADD THIS NEW ONE FOR DIRECT AI ANALYSIS
    @PostMapping("/analyze-plant")
    public String analyzePlantOnly(@RequestParam("plantImage") MultipartFile file, Model model) {
        try {
            if (file.isEmpty()) {
                model.addAttribute("error", "Please select an image file");
                return "index";
            }

            Map<String, Object> aiResult = service.analyzePlantDisease(file);

            // Create a basic plant object for the template
            Plant plant = new Plant();
            plant.setDisease((String) aiResult.get("disease"));
            plant.setConfidence((Double) aiResult.get("confidence"));
            plant.setHealthy((Boolean) aiResult.get("is_healthy"));

            model.addAttribute("plant", plant);
            model.addAttribute("aiResult", aiResult);

            // ADD SEPARATE ATTRIBUTES FOR THIS METHOD TOO
            model.addAttribute("disease", plant.getDisease());
            model.addAttribute("confidence", plant.getConfidence());
            model.addAttribute("isHealthy", plant.getHealthy());
            model.addAttribute("topPredictions", aiResult.get("top_predictions"));

            return "plant-result";

        } catch (Exception e) {
            model.addAttribute("error", "Error analyzing image: " + e.getMessage());
            return "index";
        }
    }
}