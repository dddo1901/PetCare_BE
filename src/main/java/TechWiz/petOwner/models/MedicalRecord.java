package TechWiz.petOwner.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medical_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;
    
    @Column(name = "record_type", nullable = false, length = 100)
    private String recordType; // CHECKUP, TREATMENT, EMERGENCY, SURGERY, VACCINATION, etc.
    
    @Column(columnDefinition = "TEXT")
    private String diagnosis;
    
    @Column(columnDefinition = "TEXT")
    private String treatment;
    
    @Column(columnDefinition = "TEXT")
    private String medications;
    
    @Column(name = "veterinarian_id", nullable = false)
    private Long veterinarianId;
    
    @Column(name = "vet_name", length = 100)
    private String vetName;
    
    @Column(name = "clinic_name", length = 200)
    private String clinicName;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal cost;
    
    @Column(name = "follow_up_required", nullable = false)
    private Boolean followUpRequired = false;
    
    @Column(name = "follow_up_date")
    private LocalDate followUpDate;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments; // JSON array of file URLs
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    @JsonBackReference
    private Pet pet;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
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
