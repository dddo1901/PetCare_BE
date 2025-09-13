package TechWiz.admin.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stockQuantity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetType targetPetType;
    
    @Column(length = 100)
    private String brand;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(name = "additional_images", columnDefinition = "TEXT")
    private String additionalImages; // JSON array of image URLs
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private Boolean isAvailable = true;
    
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Product specifications (weight, dimensions, etc.)
    @Column(name = "weight_kg", precision = 8)
    private BigDecimal weight;
    
    @Column(name = "dimensions_cm", length = 100)
    private String dimensions; // Format: "L x W x H"
    
    @Column(name = "age_group", length = 50)
    private String ageGroup; // Puppy, Adult, Senior, All Ages
    
    @Column(name = "special_features", columnDefinition = "TEXT")
    private String specialFeatures; // JSON array of features
    
    // Additional product specifications  
    @Column(name = "specifications", columnDefinition = "TEXT")
    private String specifications; // JSON string of specifications
    
    @Column(name = "ingredients", columnDefinition = "TEXT") 
    private String ingredients; // JSON string of ingredients
    
    @Column(name = "usage_instructions", columnDefinition = "TEXT")
    private String usageInstructions; // JSON string of usage instructions
    
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
