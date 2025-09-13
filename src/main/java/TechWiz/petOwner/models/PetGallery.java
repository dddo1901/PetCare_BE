package TechWiz.petOwner.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet_gallery")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetGallery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long petId;
    
    @Column(nullable = false)
    private Long ownerId;
    
    @Column(nullable = false, length = 500)
    private String imageUrl;
    
    @Column(length = 200)
    private String caption;
    
    @Column(nullable = false)
    private Integer displayOrder = 0;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
