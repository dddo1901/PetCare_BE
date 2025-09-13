package TechWiz.shelter.models;

import java.time.LocalDateTime;

import jakarta.persistence.*;
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
    private Pet pet;
    
    // Relationship with shelter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id", nullable = false)
    private Shelter shelter;
    
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
