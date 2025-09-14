package TechWiz.veterinarian.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "veterinarian_medical_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarianMedicalRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long vetId;
    
    @Column(nullable = false)
    private Long petId;
    
    @Column(nullable = false)
    private Long ownerId;
    
    @Column(nullable = false)
    private Long appointmentId; // Reference to the appointment
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordType type;
    
    @Column(nullable = false, length = 200)
    private String diagnosis;
    
    @Column(columnDefinition = "TEXT")
    private String treatment;
    
    @Column(columnDefinition = "TEXT")
    private String medication;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(columnDefinition = "TEXT")
    private String followUpInstructions;
    
    @Column(precision = 10)
    private Double treatmentCost;
    
    @Column(nullable = false)
    private LocalDateTime recordDate;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum RecordType {
        EXAMINATION("Examination"),
        TREATMENT("Treatment"),
        SURGERY("Surgery"),
        VACCINATION("Vaccination"),
        FOLLOW_UP("Follow-up");
        
        private final String displayName;
        
        RecordType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
