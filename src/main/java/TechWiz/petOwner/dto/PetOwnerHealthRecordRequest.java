package TechWiz.petOwner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PetOwnerHealthRecordRequest {
    
    @NotNull(message = "Pet ID is required")
    private Long petId;
    
    @NotBlank(message = "Record type is required")
    private String type;
    
    private String description;
    
    private String vet;
    
    private String clinic;
    
    private BigDecimal cost;
    
    private String notes;
    
    @NotNull(message = "Record date is required")
    private LocalDateTime recordDate;
}
