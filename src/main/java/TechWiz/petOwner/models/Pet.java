package TechWiz.petOwner.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 50)
    private String type; // DOG, CAT, BIRD, etc.
    
    @Column(length = 100)
    private String breed;
    
    @Column(nullable = false)
    private Integer age;
    
    @Column(length = 20)
    private String gender; // MALE, FEMALE, UNKNOWN
    
    private Double weight;
    
    @Column(length = 50)
    private String color;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "microchip_id", length = 50)
    private String microchipId;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "health_status", length = 50, nullable = false)
    private String healthStatus = "HEALTHY"; // HEALTHY, SICK, RECOVERING, NEEDS_MONITORING
    
    @Column(name = "last_checkup")
    private LocalDate lastCheckup;
    
    @Column(name = "next_vaccination")
    private LocalDate nextVaccination;
    
    @Column(name = "vet_name", length = 100)
    private String vetName;
    
    @Column(name = "vet_phone", length = 20)
    private String vetPhone;
    
    @Column(name = "insurance_provider", length = 100)
    private String insuranceProvider;
    
    @Column(name = "policy_number", length = 50)
    private String policyNumber;
    
    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;
    
    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;
    
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Gallery photos stored as comma-separated URLs
    @Column(name = "photos", columnDefinition = "TEXT")
    private String photos;
    
    // Relationships
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<MedicalRecord> medicalRecords;
    
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Vaccination> vaccinations;
    
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Medication> medications;
    
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Appointment> appointments;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
