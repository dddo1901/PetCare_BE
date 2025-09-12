package TechWiz.veterinarian.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import TechWiz.auths.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "VetVeterinarianProfile")
@Table(name = "veterinarian_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarianProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonBackReference
    private User user;
    
    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;
    
    @Column(length = 100)
    private String clinic;
    
    @Column(name = "clinic_address", columnDefinition = "TEXT")
    private String clinicAddress;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(columnDefinition = "JSON")
    private String specialties; // JSON array of specialties
    
    @Column(columnDefinition = "JSON")
    private String education; // JSON array of education details
    
    @Column(columnDefinition = "JSON")
    private String experience; // JSON array of experience details
    
    @Column(columnDefinition = "JSON")
    private String languages; // JSON array of languages
    
    @Column(name = "consultation_fee")
    private BigDecimal consultationFee;
    
    @Column(name = "emergency_fee")
    private BigDecimal emergencyFee;
    
    @Column(columnDefinition = "JSON")
    private String workingHours; // JSON object with working hours
    
    @Column(name = "profile_image", length = 500)
    private String profileImage;
    
    @Column(nullable = false)
    private Double rating = 0.0;
    
    @Column(name = "total_reviews", nullable = false)
    private Integer totalReviews = 0;
    
    @Column(name = "years_experience")
    private Integer yearsExperience;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
    
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id")
    @JsonManagedReference
    private List<VetAppointment> appointments;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id")
    @JsonManagedReference
    private List<VetMedicalRecord> medicalRecords;
    
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
