package TechWiz.admin.models.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
    
    private String reason; // Optional reason for deactivation
}
