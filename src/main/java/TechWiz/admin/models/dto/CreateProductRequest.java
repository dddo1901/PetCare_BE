package TechWiz.admin.models.dto;

import java.math.BigDecimal;

import TechWiz.admin.models.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateProductRequest {
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Category is required")
    private ProductCategory category;
    
    private String brand;
    private String imageUrl;
    private Boolean isActive;
}
