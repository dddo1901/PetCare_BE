package TechWiz.petOwner.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMedicalRecordRequest {
    
    @NotNull(message = "Pet ID is required")
    private Long petId;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotBlank(message = "Type is required")
    private String type; // Regular Checkup, Treatment, Emergency
    
    private String description;
    private String vetName;
    private String clinicName;
    private BigDecimal cost;
    private String status;
    private String notes;
}
