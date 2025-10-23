/*
package com.example.planthealth.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private String imagePath;
    private String healthStatus;
    private Double confidenceScore;
}
*/
package com.example.planthealth.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private String imagePath;
    private String healthStatus;
    private Double confidenceScore;

    // NEW FIELDS FOR AI DISEASE DETECTION
    private String disease;
    private Double confidence;
    private Boolean healthy;

    // Optional: Helper method to check if plant is healthy (not stored in DB)
    @Transient
    public boolean isPlantHealthy() {
        return healthy != null && healthy;
    }
}