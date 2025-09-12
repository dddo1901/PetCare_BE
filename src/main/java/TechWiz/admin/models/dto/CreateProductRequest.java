package TechWiz.admin.models.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import TechWiz.admin.models.ProductCategory;
import TechWiz.admin.models.PetType;

@Data
public class CreateProductRequest {
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
    
    @NotNull(message = "Category is required")
    private ProductCategory category;
    
    @NotNull(message = "Target pet type is required")
    private PetType targetPetType;
    
    private String brand;
    private String imageUrl;
    private String additionalImages; // JSON string
    
    // Product specifications
    private String specifications; // JSON string
    private String ingredients; // JSON string  
    private String usageInstructions; // JSON string
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
    
    // Optional specifications
    private BigDecimal weight;
    private String dimensions;
    private String ageGroup;
    private String specialFeatures; // JSON string
}
