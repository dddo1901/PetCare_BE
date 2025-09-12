package TechWiz.petOwner.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {
    
    @NotNull(message = "Pet ID is required")
    private Long petId;
    
    @NotNull(message = "Veterinarian ID is required")
    private Long veterinarianId;
    
    @NotNull(message = "Appointment date and time is required")
    private LocalDateTime appointmentDateTime;
    
    private String reason;
    
    private String notes;
    
    private String priority; // HIGH, NORMAL, LOW
}
