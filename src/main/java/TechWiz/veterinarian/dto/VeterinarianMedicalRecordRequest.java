package TechWiz.veterinarian.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarianMedicalRecordRequest {
    
    @NotNull(message = "Pet ID is required")
    private Long petId;
    
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;
    
    @NotNull(message = "Record type is required")
    private String type; // EXAMINATION, TREATMENT, SURGERY, VACCINATION, FOLLOW_UP
    
    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;
    
    private String treatment;
    
    private String medication;
    
    private String notes;
    
    private String followUpInstructions;
    
    private Double treatmentCost;
    
    @NotNull(message = "Record date is required")
    private LocalDateTime recordDate;
}
