package TechWiz.shelter.models;

import java.time.LocalDateTime;

import TechWiz.auths.models.ShelterProfile;
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "adoption_inquiries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionInquiry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String adopterName;
    
    @Column(nullable = false, length = 100)
    private String adopterEmail;
    
    @Column(nullable = false, length = 20)
    private String adopterPhone;
    
    @Column(length = 500)
    private String adopterAddress;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(columnDefinition = "TEXT")
    private String livingSituation;
    
    @Column(columnDefinition = "TEXT")
    private String petExperience;
    
    @Column(nullable = false)
    private Boolean hasYard = false;
    
    @Column(nullable = false)
    private Boolean hasOtherPets = false;
    
    @Column(nullable = false)
    private Boolean hasChildren = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryStatus status = InquiryStatus.NEW;
    
    @Column(columnDefinition = "TEXT")
    private String shelterResponse;
    
    @Column
    private LocalDateTime respondedAt;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Relationship with pet
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private ShelterPet pet;
    
    // Relationship with shelter profile
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_profile_id", nullable = false)
    private ShelterProfile shelterProfile;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum InquiryStatus {
        NEW,
        CONTACTED,
        IN_REVIEW,
        APPROVED,
        REJECTED,
        COMPLETED
    }
}
