package TechWiz.shelter.models;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "care_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CareLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CareType type;
    
    @Column(nullable = false)
    private LocalDateTime careDate;
    
    @Column(nullable = false)
    private LocalTime careTime;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String details;
    
    @Column(nullable = false, length = 100)
    private String staffName;
    
    @Column(length = 500)
    private String notes;
    
    @ElementCollection
    @CollectionTable(name = "care_log_attachments", joinColumns = @JoinColumn(name = "care_log_id"))
    @Column(name = "attachment_url")
    private List<String> attachments;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Relationship with pet
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum CareType {
        FEEDING,
        GROOMING,
        MEDICAL,
        EXERCISE,
        TRAINING,
        OTHER
    }
}
