package TechWiz.petOwner.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name; // Heartgard Plus, Frontline Plus, etc.
    
    @Column(nullable = false, length = 100)
    private String type; // Heartworm prevention, Flea & tick, etc.
    
    @Column(nullable = false, length = 200)
    private String dosage; // 1 tablet/month, etc.
    
    @Column(name = "last_given")
    private LocalDate lastGiven;
    
    @Column(name = "next_due")
    private LocalDate nextDue;
    
    @Column(nullable = false, length = 50)
    private String status; // Active, Discontinued, Completed
    
    @Column(name = "prescribed_by", length = 100)
    private String prescribedBy; // Vet name
    
    @Column(columnDefinition = "TEXT")
    private String instructions;
    
    @Column(columnDefinition = "TEXT")
    private String sideEffects;
    
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
