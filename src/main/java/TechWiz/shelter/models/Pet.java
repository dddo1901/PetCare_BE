package TechWiz.shelter.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import TechWiz.auths.models.ShelterProfile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "ShelterPet")
@Table(name = "shelter_pets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetType type;
    
    @Column(nullable = false, length = 100)
    private String breed;
    
    @Column(nullable = false)
    private Integer ageInMonths;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Size size;
    
    @Column(length = 50)
    private String color;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal weight;
    
    @Column(length = 500)
    private String imageUrl;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdoptionStatus adoptionStatus = AdoptionStatus.AVAILABLE;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HealthStatus healthStatus = HealthStatus.HEALTHY;
    
    @Column(nullable = false)
    private Boolean vaccinated = false;
    
    @Column(nullable = false)
    private Boolean spayedNeutered = false;
    
    @Column(nullable = false)
    private Boolean microchipped = false;
    
    @Column(nullable = false)
    private Boolean houseTrained = false;
    
    @Column(nullable = false)
    private Boolean goodWithKids = false;
    
    @Column(nullable = false)
    private Boolean goodWithPets = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnergyLevel energyLevel = EnergyLevel.MEDIUM;
    
    @Column(length = 500)
    private String specialNeeds;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal adoptionFee;
    
    @Column(length = 1000)
    private String personality;
    
    @Column(length = 1000)
    private String requirements;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Relationship with shelter profile
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_profile_id", nullable = false)
    private ShelterProfile shelterProfile;
    
    // Relationship with care logs
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CareLog> careLogs;
    
    // Relationship with adoption inquiries
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdoptionInquiry> adoptionInquiries;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum PetType {
        DOG, CAT, BIRD, RABBIT, OTHER
    }
    
    public enum Gender {
        MALE, FEMALE
    }
    
    public enum Size {
        SMALL, MEDIUM, LARGE, EXTRA_LARGE
    }
    
    public enum AdoptionStatus {
        AVAILABLE, PENDING, ADOPTED, UNAVAILABLE
    }
    
    public enum HealthStatus {
        HEALTHY, NEEDS_MONITORING, SICK, RECOVERING
    }
    
    public enum EnergyLevel {
        LOW, MEDIUM, HIGH, VERY_HIGH
    }
}
