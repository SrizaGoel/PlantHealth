
/*package com.example.planthealth.service;

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

    private final String mlApiUrl = "http://127.0.0.1:5000/predict";

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
*/
/*
package com.example.planthealth.service;

import com.example.planthealth.model.Plant;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Service
public class PlantService {

    private final String mlApiUrl = "http://127.0.0.1:5000/predict"; // Your Flask server

    public Plant analyzePlant(Plant plant) {
        try {
            File imageFile = new File(plant.getImagePath());
            if (!imageFile.exists()) {
                plant.setHealthStatus("Error: file not found");
                plant.setConfidenceScore(0.0);
                return plant;
            }

            // Prepare multipart request
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new FileSystemResource(imageFile));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Send request to Flask
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<MLResponse> response = restTemplate.postForEntity(mlApiUrl, requestEntity, MLResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                MLResponse mlResponse = response.getBody();
                plant.setHealthStatus(mlResponse.getStatus()); // Set healthStatus from status
                plant.setConfidenceScore(mlResponse.getConfidence()); // Set confidenceScore from confidence
            }

        } catch (Exception e) {
            plant.setHealthStatus("Error connecting to ML server");
            plant.setConfidenceScore(0.0);
            e.printStackTrace();
        }

        return plant;
    }

    // Inner class for parsing Flask JSON response
    static class MLResponse {
        private String status; // This matches Flask response
        private Double confidence; // This matches Flask response

        // Getters and setters
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
*/

package com.example.planthealth.service;

import com.example.planthealth.model.Plant;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Service
public class PlantService {

    private final String mlApiUrl = "http://127.0.0.1:5000/predict";

    public Plant analyzePlant(Plant plant) {
        try {
            System.out.println("=== PlantService.analyzePlant() called ===");
            System.out.println("📁 Image path: " + plant.getImagePath());

            File imageFile = new File(plant.getImagePath());
            if (!imageFile.exists()) {
                System.err.println("❌ ERROR: Image file not found: " + plant.getImagePath());
                plant.setHealthStatus("Error: file not found");
                plant.setConfidenceScore(0.0);
                return plant;
            }

            System.out.println("✅ Image file exists, size: " + imageFile.length() + " bytes");

            // Prepare multipart request
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new FileSystemResource(imageFile));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Send request to Flask
            RestTemplate restTemplate = new RestTemplate();
            System.out.println("🔄 Sending request to Flask server: " + mlApiUrl);

            ResponseEntity<MLResponse> response = restTemplate.postForEntity(mlApiUrl, requestEntity, MLResponse.class);

            System.out.println("✅ Flask response status: " + response.getStatusCode());
            System.out.println("📨 Flask response body: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                MLResponse mlResponse = response.getBody();
                plant.setHealthStatus(mlResponse.getStatus());
                plant.setConfidenceScore(mlResponse.getConfidence());
                System.out.println("🎉 Analysis result - Status: " + mlResponse.getStatus() + ", Confidence: "
                        + mlResponse.getConfidence());
            } else {
                System.err.println("❌ Flask returned non-OK status");
                plant.setHealthStatus("Analysis failed");
                plant.setConfidenceScore(0.0);
            }

        } catch (Exception e) {
            System.err.println("❌ ERROR in PlantService: " + e.getMessage());
            e.printStackTrace();
            plant.setHealthStatus("Error connecting to ML server: " + e.getMessage());
            plant.setConfidenceScore(0.0);
        }

        return plant;
    }

    // Inner class for parsing Flask JSON response
    public static class MLResponse {
        private String status;
        private Double confidence;
        private String message;
        private String error;

        // Getters and setters
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

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        @Override
        public String toString() {
            return String.format("MLResponse{status='%s', confidence=%s, message='%s', error='%s'}",
                    status, confidence, message, error);
        }
    }
}