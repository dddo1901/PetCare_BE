package TechWiz.petOwner.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet_owner_appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetOwnerAppointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long ownerId;
    
    @Column(nullable = false)
    private Long petId;
    
    @Column(nullable = false)
    private Long vetId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;
    
    @Column(nullable = false, length = 100)
    private String type;
    
    @Column(length = 500)
    private String reason;
    
    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;
    
    @Column(columnDefinition = "TEXT")
    private String vetNotes;
    
    @Column(columnDefinition = "TEXT")
    private String ownerNotes;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum AppointmentStatus {
        PENDING("Pending"),
        CONFIRMED("Confirmed"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled"),
        RESCHEDULED("Rescheduled");
        
        private final String displayName;
        
        AppointmentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
