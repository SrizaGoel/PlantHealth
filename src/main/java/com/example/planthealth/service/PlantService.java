package com.example.planthealth.service;

import com.example.planthealth.model.Plant;
import com.example.planthealth.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

@Service
public class PlantService {

    @Autowired
    private PlantRepository repository;

    private final String mlApiUrl = "http://localhost:5000/predict";

    public Plant analyzePlant(Plant plant) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new FileSystemResource(new File(plant.getImagePath())));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<MLResponse> response = restTemplate.postForEntity(mlApiUrl, requestEntity, MLResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                plant.setHealthStatus(response.getBody().getStatus());
                plant.setConfidenceScore(response.getBody().getConfidence());
            } else {
                plant.setHealthStatus("Unknown");
                plant.setConfidenceScore(0.0);
            }
        } catch (Exception e) {
            plant.setHealthStatus("Error");
            plant.setConfidenceScore(0.0);
            e.printStackTrace();
        }

        return repository.save(plant);
    }

    static class MLResponse {
        private String status;
        private Double confidence;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Double getConfidence() {
            return confidence;
        }

        public void setConfidence(Double confidence) {
            this.confidence = confidence;
        }
    }
}
