package TechWiz.petOwner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PetOwnerAppointmentRequest {
    
    @NotNull(message = "Pet ID is required")
    private Long petId;
    
    @NotNull(message = "Vet ID is required")
    private Long vetId;
    
    @NotBlank(message = "Appointment type is required")
    private String type;
    
    private String reason;
    
    @NotNull(message = "Appointment date and time is required")
    private LocalDateTime appointmentDateTime;
    
    private String ownerNotes;
}
