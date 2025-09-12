package TechWiz.petOwner.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String status; // SCHEDULED, CONFIRMED, COMPLETED, CANCELLED, RESCHEDULED
    
    @Column(nullable = false, length = 100)
    private String type; // General Checkup, Vaccination, Treatment
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(name = "appointment_datetime", nullable = false)
    private LocalDateTime appointmentDateTime;
    
    @Column(name = "veterinarian_id", nullable = false)
    private Long veterinarianId;
    
    @Column(name = "vet_name", length = 100)
    private String vetName;
    
    @Column(name = "vet_specialty", length = 100)
    private String vetSpecialty;
    
    @Column(name = "vet_avatar", length = 500)
    private String vetAvatar;
    
    @Column(name = "location", length = 200)
    private String location;
    
    @Column(name = "vet_phone", length = 20)
    private String vetPhone;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal fee;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "priority", length = 20, nullable = false)
    private String priority = "NORMAL"; // HIGH, NORMAL, LOW
    
    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;
    
    @Column(name = "outcome", columnDefinition = "TEXT")
    private String outcome;
    
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
