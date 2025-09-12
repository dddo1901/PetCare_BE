package TechWiz.admin.models.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateProductStatusRequest {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    private Boolean isActive;
    
    private Boolean isAvailable;
    
    private String reason; // Optional reason for deactivation
}
