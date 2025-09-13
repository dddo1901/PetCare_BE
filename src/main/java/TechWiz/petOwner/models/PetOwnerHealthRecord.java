package TechWiz.petOwner.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet_owner_health_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetOwnerHealthRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long petId;
    
    @Column(nullable = false)
    private Long ownerId;
    
    @Column(nullable = false, length = 100)
    private String type;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 200)
    private String vet;
    
    @Column(length = 200)
    private String clinic;
    
    @Column(precision = 10)
    private BigDecimal cost;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
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
}
