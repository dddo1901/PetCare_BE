package TechWiz.petOwner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PetGalleryRequest {
    
    @NotNull(message = "Pet ID is required")
    private Long petId;
    
    @NotBlank(message = "Image URL is required")
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;
    
    @Size(max = 200, message = "Caption must not exceed 200 characters")
    private String caption;
    
    private Integer displayOrder = 0;
}
