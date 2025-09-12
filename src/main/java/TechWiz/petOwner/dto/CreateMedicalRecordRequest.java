package TechWiz.petOwner.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMedicalRecordRequest {
    
    @NotNull(message = "Pet ID is required")
    private Long petId;
    
    @NotNull(message = "Veterinarian ID is required")
    private Long veterinarianId;
    
    @NotNull(message = "Visit date is required")
    private LocalDate visitDate;
    
    @NotNull(message = "Record type is required")
    private String recordType; // CHECKUP, TREATMENT, EMERGENCY, SURGERY, VACCINATION
    
    private String diagnosis;
    
    private String treatment;
    
    private String medications;
    
    private String notes;
    
    private Boolean followUpRequired;
    
    private LocalDate followUpDate;
    
    private BigDecimal cost;
}
