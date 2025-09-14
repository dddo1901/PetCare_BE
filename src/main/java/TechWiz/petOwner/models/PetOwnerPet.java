package TechWiz.petOwner.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet_owner_pets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetOwnerPet {
    
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
    
    @Column(length = 50)
    private String color;
    
    @Column(precision = 5)
    private BigDecimal weight;
    
    @Column(length = 100)
    private String microchip;
    
    @Column(name = "image_url", columnDefinition = "LONGTEXT")
    private String imageUrl;
    
    @Column(name = "photos", columnDefinition = "LONGTEXT")
    private String photos; // JSON array of photo URLs
    
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    
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
    
    @Column(columnDefinition = "LONGTEXT")
    private String specialNeeds;
    
    @Column(length = 1000)
    private String personality;
    
    @Column(length = 1000)
    private String requirements;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private Long ownerId;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum PetType {
        DOG("Dog"),
        CAT("Cat"),
        BIRD("Bird"),
        FISH("Fish"),
        RABBIT("Rabbit"),
        HAMSTER("Hamster"),
        REPTILE("Reptile"),
        OTHER("Other");
        
        private final String displayName;
        
        PetType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum Gender {
        MALE("Male"),
        FEMALE("Female");
        
        private final String displayName;
        
        Gender(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum HealthStatus {
        HEALTHY("Healthy"),
        NEEDS_MONITORING("Needs Monitoring"),
        SICK("Sick"),
        RECOVERING("Recovering");
        
        private final String displayName;
        
        HealthStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum EnergyLevel {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        VERY_HIGH("Very High");
        
        private final String displayName;
        
        EnergyLevel(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}

