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
